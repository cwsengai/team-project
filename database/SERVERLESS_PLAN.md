# Serverless Architecture Plan - Direct Client to Database

## Overview

This plan outlines how to implement a **serverless architecture** where the client (your Java Swing application or future web client) communicates directly with Supabase PostgreSQL using authenticated calls, eliminating the need for a backend server.

## Architecture Choice: Supabase + Row Level Security (RLS)

### Why Supabase?

- **Built-in authentication** (email/password, OAuth, magic links)
- **Row Level Security (RLS)** policies enforce data access at the database level
- **Auto-generated REST API** for all tables
- **Real-time subscriptions** (optional)
- **Connection pooling** handled automatically
- **Free tier** suitable for development/small projects

## Security Model: Row Level Security (RLS)

### Key Concept

Instead of a backend server checking permissions, PostgreSQL itself enforces who can access what data using policies.

```sql
-- Example: Users can only see their own portfolios
CREATE POLICY "Users can view own portfolios"
ON portfolios FOR SELECT
USING (auth.uid() = user_id);

-- Example: Users can only insert their own trades
CREATE POLICY "Users can create own trades"
ON trades FOR INSERT
WITH CHECK (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);
```

---

## Implementation Steps

### Step 1: Supabase Setup (30 minutes)

#### 1.1 Create Supabase Project

1. Go to [https://supabase.com](https://supabase.com)
2. Sign up and create a new project
3. Choose project name: `portfolio-tracker`
4. Set a strong database password
5. Select closest region

#### 1.2 Run Schema with Modifications

We need to modify the schema to work with Supabase's auth system:

```sql
-- Supabase provides auth.users automatically
-- We create a public.users table for additional profile data

-- 1) Public users table (extends auth.users)
CREATE TABLE public.user_profiles (
  id uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  display_name text,
  created_at timestamptz DEFAULT now(),
  last_login timestamptz
);

-- Note: email and password are in auth.users (managed by Supabase)
-- We only store additional profile information

-- 2) Sessions table NOT needed - Supabase handles this with JWTs

-- 3-9) All other tables remain the same, but with modified foreign keys
-- Change user_id references from public.users to auth.users
```

#### 1.3 Get API Credentials

From Supabase dashboard → Settings → API:

- `SUPABASE_URL`: `https://your-project.supabase.co`
- `SUPABASE_ANON_KEY`: Public anonymous key (safe to expose)
- `SERVICE_ROLE_KEY`: Admin key (NEVER expose to client)

---

### Step 2: Enable Row Level Security

Run this in Supabase SQL Editor:

```sql
-- Enable RLS on all tables
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE companies ENABLE ROW LEVEL SECURITY;
ALTER TABLE price_points ENABLE ROW LEVEL SECURITY;
ALTER TABLE candles ENABLE ROW LEVEL SECURITY;
ALTER TABLE portfolios ENABLE ROW LEVEL SECURITY;
ALTER TABLE portfolio_positions ENABLE ROW LEVEL SECURITY;
ALTER TABLE trades ENABLE ROW LEVEL SECURITY;
ALTER TABLE portfolio_snapshots ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- USER PROFILES POLICIES
-- ============================================================================

-- Users can view their own profile
CREATE POLICY "Users can view own profile"
ON user_profiles FOR SELECT
USING (auth.uid() = id);

-- Users can update their own profile
CREATE POLICY "Users can update own profile"
ON user_profiles FOR UPDATE
USING (auth.uid() = id);

-- Users can insert their own profile (on signup)
CREATE POLICY "Users can create own profile"
ON user_profiles FOR INSERT
WITH CHECK (auth.uid() = id);

-- ============================================================================
-- COMPANIES & PRICES (PUBLIC READ)
-- ============================================================================

-- Anyone authenticated can read company data
CREATE POLICY "Authenticated users can view companies"
ON companies FOR SELECT
TO authenticated
USING (true);

-- Only admins can modify companies (using service role key)
-- No policy = blocked by RLS for regular users

-- Anyone authenticated can read price data
CREATE POLICY "Authenticated users can view prices"
ON price_points FOR SELECT
TO authenticated
USING (true);

CREATE POLICY "Authenticated users can view candles"
ON candles FOR SELECT
TO authenticated
USING (true);

-- Price data can be inserted by service (background job)
-- Or use service role key for price updates

-- ============================================================================
-- PORTFOLIOS POLICIES
-- ============================================================================

-- Users can only view their own portfolios
CREATE POLICY "Users can view own portfolios"
ON portfolios FOR SELECT
USING (auth.uid() = user_id);

-- Users can create their own portfolios
CREATE POLICY "Users can create own portfolios"
ON portfolios FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Users can update their own portfolios
CREATE POLICY "Users can update own portfolios"
ON portfolios FOR UPDATE
USING (auth.uid() = user_id);

-- Users can delete their own portfolios
CREATE POLICY "Users can delete own portfolios"
ON portfolios FOR DELETE
USING (auth.uid() = user_id);

-- ============================================================================
-- POSITIONS POLICIES
-- ============================================================================

-- Users can view positions in their portfolios
CREATE POLICY "Users can view own positions"
ON portfolio_positions FOR SELECT
USING (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Users can create positions in their portfolios
CREATE POLICY "Users can create own positions"
ON portfolio_positions FOR INSERT
WITH CHECK (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Users can update positions in their portfolios
CREATE POLICY "Users can update own positions"
ON portfolio_positions FOR UPDATE
USING (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Users can delete positions in their portfolios
CREATE POLICY "Users can delete own positions"
ON portfolio_positions FOR DELETE
USING (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- ============================================================================
-- TRADES POLICIES
-- ============================================================================

-- Users can view trades in their portfolios
CREATE POLICY "Users can view own trades"
ON trades FOR SELECT
USING (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Users can create trades in their portfolios
CREATE POLICY "Users can create own trades"
ON trades FOR INSERT
WITH CHECK (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Trades are immutable (no update/delete policies)
-- If needed, add soft delete or audit trail

-- ============================================================================
-- SNAPSHOTS POLICIES
-- ============================================================================

-- Users can view snapshots of their portfolios
CREATE POLICY "Users can view own snapshots"
ON portfolio_snapshots FOR SELECT
USING (
  portfolio_id IN (
    SELECT id FROM portfolios WHERE user_id = auth.uid()
  )
);

-- Snapshots are typically created by background jobs
-- Users shouldn't create/modify directly
```

---

### Step 3: Client Implementation (Java)

#### 3.1 Add Supabase Java Client Dependency

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Supabase Java Client -->
    <dependency>
        <groupId>io.github.jan-tennert.supabase</groupId>
        <artifactId>supabase-kt</artifactId>
        <version>2.0.0</version>
    </dependency>

    <!-- Or use REST API directly with OkHttp -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.12.0</version>
    </dependency>

    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

#### 3.2 Create Supabase Client Wrapper

```java
package data_access;

import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://your-project.supabase.co";
    private static final String SUPABASE_ANON_KEY = "your-anon-key";

    private final OkHttpClient httpClient;
    private final Gson gson;
    private String accessToken; // JWT from auth

    public SupabaseClient() {
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    // Authentication
    public AuthResponse signUp(String email, String password) throws IOException {
        String json = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}",
            email, password
        );

        RequestBody body = RequestBody.create(
            json,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(SUPABASE_URL + "/auth/v1/signup")
            .header("apikey", SUPABASE_ANON_KEY)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);
            this.accessToken = authResponse.access_token;
            return authResponse;
        }
    }

    public AuthResponse signIn(String email, String password) throws IOException {
        String json = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}",
            email, password
        );

        RequestBody body = RequestBody.create(
            json,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
            .header("apikey", SUPABASE_ANON_KEY)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);
            this.accessToken = authResponse.access_token;
            return authResponse;
        }
    }

    public void signOut() {
        this.accessToken = null;
    }

    // Generic database query
    public <T> T query(String table, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
            .url(SUPABASE_URL + "/rest/v1/" + table)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer " + accessToken)
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), responseType);
        }
    }

    // Insert data
    public <T> T insert(String table, Object data, Class<T> responseType) throws IOException {
        String json = gson.toJson(data);

        RequestBody body = RequestBody.create(
            json,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(SUPABASE_URL + "/rest/v1/" + table)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer " + accessToken)
            .header("Prefer", "return=representation")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), responseType);
        }
    }

    // Update data
    public <T> T update(String table, String filter, Object data, Class<T> responseType)
        throws IOException {
        String json = gson.toJson(data);

        RequestBody body = RequestBody.create(
            json,
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(SUPABASE_URL + "/rest/v1/" + table + "?" + filter)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer " + accessToken)
            .header("Prefer", "return=representation")
            .patch(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), responseType);
        }
    }
}

class AuthResponse {
    public String access_token;
    public String refresh_token;
    public String token_type;
    public int expires_in;
    public User user;

    static class User {
        public String id;
        public String email;
    }
}
```

#### 3.3 Implement Repository with Supabase

```java
package data_access;

import entity.Portfolio;
import java.util.List;
import java.util.Optional;

public class SupabasePortfolioRepository implements PortfolioRepository {
    private final SupabaseClient client;

    public SupabasePortfolioRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<Portfolio> findById(String id) {
        try {
            // Query: GET /rest/v1/portfolios?id=eq.{id}
            Portfolio[] portfolios = client.query(
                "portfolios?id=eq." + id,
                Portfolio[].class
            );

            if (portfolios != null && portfolios.length > 0) {
                return Optional.of(portfolios[0]);
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching portfolio", e);
        }
    }

    @Override
    public List<Portfolio> findByUserId(String userId) {
        try {
            // RLS automatically filters by auth.uid()
            // So we just query all portfolios - only user's will be returned
            Portfolio[] portfolios = client.query(
                "portfolios",
                Portfolio[].class
            );

            return List.of(portfolios);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching portfolios", e);
        }
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        try {
            // Supabase will auto-generate UUID if not provided
            Portfolio[] result = client.insert(
                "portfolios",
                portfolio,
                Portfolio[].class
            );

            return result[0];

        } catch (Exception e) {
            throw new RuntimeException("Error saving portfolio", e);
        }
    }

    @Override
    public void updateCash(String id, double cash) {
        try {
            String updateData = String.format("{\"current_cash\": %f}", cash);
            client.update(
                "portfolios",
                "id=eq." + id,
                updateData,
                Portfolio[].class
            );

        } catch (Exception e) {
            throw new RuntimeException("Error updating cash", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // RLS will prevent deleting portfolios user doesn't own
            RequestBody body = RequestBody.create("", null);

            Request request = new Request.Builder()
                .url(client.getBaseUrl() + "/rest/v1/portfolios?id=eq." + id)
                .header("apikey", client.getAnonKey())
                .header("Authorization", "Bearer " + client.getAccessToken())
                .delete()
                .build();

            client.execute(request);

        } catch (Exception e) {
            throw new RuntimeException("Error deleting portfolio", e);
        }
    }
}
```

---

### Step 4: Authentication Flow in Your App

```java
package app;

import data_access.SupabaseClient;
import javax.swing.*;

public class LoginPage extends JPanel {
    private final SupabaseClient supabase;

    public LoginPage(SupabaseClient supabase) {
        this.supabase = supabase;
        initComponents();
    }

    private void onLoginClicked() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            AuthResponse response = supabase.signIn(email, password);

            // Success - token is stored in supabase client
            // Navigate to main application
            navigateToMainApp(response.user.id);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Login failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onSignUpClicked() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            AuthResponse response = supabase.signUp(email, password);

            // Create user profile
            createUserProfile(response.user.id);

            // Navigate to main app
            navigateToMainApp(response.user.id);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Sign up failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
```

---

### Step 5: Background Price Updates (Edge Functions)

Since clients can't write to `price_points` directly (no policy), use Supabase Edge Functions for scheduled tasks:

```typescript
// supabase/functions/update-prices/index.ts
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req) => {
  const supabaseAdmin = createClient(
    Deno.env.get("SUPABASE_URL") ?? "",
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
  );

  // Fetch prices from AlphaVantage
  const tickers = ["AAPL", "GOOGL", "MSFT"];

  for (const ticker of tickers) {
    const price = await fetchPriceFromAlphaVantage(ticker);

    // Insert with service role (bypasses RLS)
    await supabaseAdmin.from("price_points").insert({
      company_id: await getCompanyId(ticker),
      timestamp: new Date(),
      interval: "DAILY",
      close: price,
      source: "AlphaVantage",
    });
  }

  return new Response(JSON.stringify({ success: true }), {
    headers: { "Content-Type": "application/json" },
  });
});
```

Schedule this to run every 15 minutes using Supabase Cron.

---

## Security Checklist

✅ **Never expose service role key to client**  
✅ **Always use RLS policies for all tables**  
✅ **Validate JWT on every request (Supabase does this)**  
✅ **Use HTTPS only (Supabase enforces this)**  
✅ **Implement rate limiting (Supabase provides this)**  
✅ **Log all sensitive operations**  
✅ **Use prepared statements (PostgREST does this)**  
✅ **Sanitize user inputs (gson handles this)**

---

## Cost Estimate (Supabase Free Tier)

- **Database**: 500 MB (enough for ~100k trades)
- **Auth users**: Unlimited
- **API requests**: 500k/month (plenty for small app)
- **Storage**: 1 GB (not needed for this app)
- **Bandwidth**: 2 GB

**Cost**: $0/month for development, ~$25/month for production if you exceed limits

---

## Advantages of This Approach

✅ **No backend server to maintain**  
✅ **No server hosting costs**  
✅ **Automatic scaling** (Supabase handles it)  
✅ **Built-in authentication**  
✅ **Database-enforced security** (can't bypass RLS)  
✅ **Real-time updates** (optional with Supabase subscriptions)  
✅ **Automatic API generation**  
✅ **Simple deployment** (just update client app)

## Disadvantages

⚠️ **Limited business logic** (mostly in client or edge functions)  
⚠️ **Complex RLS policies** can be hard to debug  
⚠️ **Less control** over API layer  
⚠️ **Vendor lock-in** to Supabase (but easy to migrate - it's just PostgreSQL)

---

## Next Steps

1. **Create Supabase account** (10 min)
2. **Run modified schema** with RLS policies (30 min)
3. **Update Java client** with SupabaseClient (2 hours)
4. **Implement authentication UI** (2 hours)
5. **Test with real data** (1 hour)
6. **Deploy** (client only - no server needed!)

Total implementation time: **~1 day** instead of weeks building a backend!
