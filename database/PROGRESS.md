# Portfolio Tracker - Serverless Implementation Progress

## 📊 Current Status: Repositories Complete ✅

**Last Updated**: November 16, 2025  
**Branch**: `database`  
**Architecture**: Clean Architecture + Supabase Serverless

**Progress**: All Supabase repository implementations complete! ✨  
**Next**: Deploy Supabase database and configure credentials

---

## ✅ Completed Tasks

### Phase 1: Entity & Schema Design

- ✅ Created complete database schema (`database/schema.sql`)

  - 9 tables: users, sessions, companies, price_points, candles, portfolios, portfolio_positions, trades, portfolio_snapshots
  - Row Level Security (RLS) policies for all tables
  - Triggers for auto-updating timestamps
  - Indexes for performance
  - Views for common queries

- ✅ Updated all entity classes to match database schema:

  - `User.java` - new entity for authentication
  - `Company.java` - new entity for stock metadata
  - `TradeType.java` - new enum (BUY/SELL)
  - `TimeInterval.java` - converted to enum (DAILY, WEEKLY, MONTHLY, INTRADAY)
  - `Portfolio.java` - added 8 new fields (userId, name, isSimulation, initialCash, currentCash, currency, timestamps)
  - `Position.java` - added 7 new fields (id, portfolioId, companyId, realizedPL, unrealizedPL, lastUpdated)
  - `Trade.java` - switched to TradeType enum, added 5 new fields (portfolioId, positionId, companyId, fees, executedAt, createdAt)
  - `PricePoint.java` - added OHLC fields (open, high, low, close, volume, source, companyId, interval)
  - `Candle.java` - fully implemented with analysis methods

- ✅ Maintained backward compatibility
  - All entities have @Deprecated methods for old API
  - Multiple constructors to support both old and new code
  - Existing code continues to compile

### Phase 2: Repository Interfaces

- ✅ Created repository interfaces following Clean Architecture:
  - `UserRepository.java` - user authentication & profile management
  - `CompanyRepository.java` - company/stock metadata
  - `PriceRepository.java` - historical & real-time price data
  - `PositionRepository.java` - portfolio positions
  - `TradeRepository.java` - buy/sell transactions
  - `PortfolioRepository.java` - portfolio CRUD (updated from stub)

### Phase 3: Supabase Infrastructure

- ✅ Added dependencies to `pom.xml`:

  - OkHttp 4.12.0 (HTTP client)
  - Gson 2.10.1 (JSON serialization)

- ✅ Implemented Supabase client infrastructure:

  - `SupabaseClient.java` - complete REST API wrapper

    - Authentication: signUp(), signIn(), signOut()
    - Database operations: query(), queryWithFilter(), insert(), update(), delete()
    - JWT token management
    - Error handling with IOException

  - `AuthResponse.java` - authentication response model

  - `SupabasePortfolioRepository.java` - complete reference implementation
    - Shows how to use SupabaseClient
    - Demonstrates RLS automatic filtering
    - Full CRUD operations via REST API
    - Proper error handling

### Phase 4: Documentation

- ✅ `database/IMPLEMENTATION_PLAN.md` - 4-phase implementation guide with repository interfaces
- ✅ `database/SERVERLESS_PLAN.md` - complete Supabase serverless architecture guide
- ✅ `database/QUERIES.md` - quick reference SQL queries
- ✅ `database/schema.sql` - production-ready PostgreSQL schema

### Phase 5: SOLID Principles Compliance

- ✅ Fixed all SOLID violations:
  - **Liskov Substitution**: Removed inheritance from StockDataGateway → PriceProvider
  - **Interface Segregation**: Removed PriceProvider dependency from Portfolio entity
  - **Dependency Inversion**: Created PortfolioView and PortfolioController interfaces

---

### Phase 6: Supabase Repository Implementations ✅

- ✅ `SupabaseUserRepository implements UserRepository`

  - findById(), save(), updateLastLogin()
  - Note: findByEmail() requires Admin API (documented in code)
  - Password management handled by Supabase Auth

- ✅ `SupabaseCompanyRepository implements CompanyRepository`

  - findByTicker(), findById(), findBySector()
  - save(), saveAll()
  - Public read access via RLS

- ✅ `SupabasePriceRepository implements PriceRepository`

  - savePricePoint(), savePricePoints()
  - getLatestPrice(), getLatestPrices()
  - getHistoricalPrices(), cleanup()
  - Optimized with company_id lookups

- ✅ `SupabasePositionRepository implements PositionRepository`

  - findByPortfolioId(), findByPortfolioAndTicker(), findByPortfolioAndCompany()
  - save(), updatePL()
  - RLS ensures users only access their positions

- ✅ `SupabaseTradeRepository implements TradeRepository`

  - save(), findByPortfolioId(), findByPositionId()
  - findByPortfolioInDateRange()
  - Immutable trades (insert only)

- ✅ Updated `InMemoryPortfolioRepository` to match new interface

  - Changed findById() to return Optional<Portfolio>
  - Changed save() to return Portfolio
  - Added findByUserId(), updateCash(), delete()

- ✅ Fixed `TrackPortfolioInteractor` to use Optional
  - Updated portfolio retrieval to use Optional pattern
  - Changed from getOwnerId() to getUserId()

---

## 🚧 Remaining Work

### Next Step: Deploy Database (Estimated: 30 minutes)

1. Create Supabase account at https://supabase.com (free tier)
2. Create new project: `portfolio-tracker`
3. Copy and run `database/schema.sql` in Supabase SQL Editor
4. Get credentials from Settings → API:
   - SUPABASE_URL
   - SUPABASE_ANON_KEY
5. Update `SupabaseClient.java` constants with your credentials (lines 17-18)

### Then: Authentication UI (Estimated: 3-4 hours)

- [ ] Create `LoginPage.java` (Swing JPanel)

  - Email/password fields
  - Login button → calls `supabaseClient.signIn()`
  - Sign up button → calls `supabaseClient.signUp()`
  - Error handling with JOptionPane

- [ ] Create `SignUpPage.java`

  - Email/password/confirm password fields
  - Validation (email format, password strength)
  - Create user profile after signup

- [ ] Update `MainFrame.java`
  - Show LoginPage on startup if not authenticated
  - Store SupabaseClient instance globally
  - Pass client to all repositories

### Then: Update Use Cases (Estimated: 4-6 hours)

- [ ] Update `TrackPortfolioInteractor`

  - Inject PortfolioRepository via constructor
  - Fetch from database instead of in-memory

- [ ] Create `ExecuteTradeInteractor`

  - Use TradeRepository, PositionRepository, PortfolioRepository
  - Implement database transaction (all-or-nothing)
  - Calculate realized P/L on sells

- [ ] Create `RegisterUserInteractor`

  - Validate email/password
  - Call supabaseClient.signUp()
  - Create user profile in database

- [ ] Create `LoginUserInteractor`
  - Call supabaseClient.signIn()
  - Update last login timestamp
  - Store JWT token

### Then: Integration Testing (Estimated: 2-3 hours)

- [ ] Test authentication flow

  - Sign up → creates user in database
  - Sign in → returns valid JWT
  - JWT expires → show login again

- [ ] Test portfolio operations

  - Create portfolio → saved to database
  - Add position → linked to portfolio
  - Execute trade → updates position and cash
  - View portfolio → loads from database with current prices

- [ ] Test RLS policies
  - User A cannot see User B's portfolios
  - User A cannot modify User B's trades
  - Price data is public (all authenticated users can read)

### Optional: Advanced Features (Estimated: 8-12 hours)

- [ ] Background price updates (Supabase Edge Functions)

  - Scheduled function to fetch AlphaVantage prices
  - Runs every 15 minutes during market hours
  - Uses service role key (bypasses RLS)

- [ ] Portfolio snapshots

  - Daily cron job to create snapshots
  - Performance charts using snapshots table

- [ ] Real-time updates (Supabase Realtime)
  - Subscribe to portfolio changes
  - Auto-refresh UI when data changes

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Swing App)                    │
├─────────────────────────────────────────────────────────────┤
│  UI Layer (view/)                                           │
│  - PortfolioPage, LoginPage, SignUpPage                    │
├─────────────────────────────────────────────────────────────┤
│  Interface Adapters (interface_adapter/)                    │
│  - Controllers (TradingController)                          │
│  - Presenters (PortfolioPresenter)                         │
│  - ViewModels (PortfolioViewModel)                         │
├─────────────────────────────────────────────────────────────┤
│  Use Cases (use_case/)                                      │
│  - TrackPortfolioInteractor                                 │
│  - ExecuteTradeInteractor                                   │
│  - RegisterUserInteractor                                   │
│  - LoginUserInteractor                                      │
├─────────────────────────────────────────────────────────────┤
│  Data Access (data_access/)                                 │
│  - Repository Interfaces (PortfolioRepository, etc.)        │
│  - Supabase Implementations (SupabasePortfolioRepo, etc.)  │
│  - SupabaseClient (REST API wrapper)                        │
│  - AlphaVantageGateway (price data fetcher)                │
├─────────────────────────────────────────────────────────────┤
│  Entities (entity/)                                         │
│  - Portfolio, Position, Trade, User, Company, PricePoint   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ HTTPS + JWT
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    SUPABASE (Serverless)                     │
├─────────────────────────────────────────────────────────────┤
│  Auth Service                                               │
│  - JWT authentication                                        │
│  - User management                                          │
├─────────────────────────────────────────────────────────────┤
│  PostgreSQL + Row Level Security (RLS)                      │
│  - users, portfolios, positions, trades, companies, prices  │
│  - RLS policies enforce data isolation per user            │
├─────────────────────────────────────────────────────────────┤
│  Edge Functions (optional)                                  │
│  - Background price updates (scheduled)                     │
│  - Portfolio snapshot creation (daily cron)                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Security Model

**Row Level Security (RLS) Policies**:

- Users can only view/edit their own portfolios, positions, and trades
- Price data and company data are public (read-only for all authenticated users)
- All policies enforced at database level (cannot be bypassed by client)

**Authentication**:

- JWT tokens issued by Supabase Auth
- Tokens passed with every database request
- Automatic token refresh handled by client

**No Backend Server Required**:

- Client talks directly to Supabase
- All business logic in client (Clean Architecture use cases)
- Database enforces security via RLS

---

## 📁 Key Files Reference

### Database

- `database/schema.sql` - Production PostgreSQL schema with RLS
- `database/SERVERLESS_PLAN.md` - Supabase implementation guide
- `database/IMPLEMENTATION_PLAN.md` - Phased implementation strategy

### Entities (Pure Business Logic)

- `entity/User.java`, `entity/Company.java` - New entities
- `entity/Portfolio.java`, `entity/Position.java`, `entity/Trade.java` - Updated
- `entity/PricePoint.java`, `entity/Candle.java`, `entity/TimeInterval.java` - Updated

### Repository Interfaces (Abstractions)

- `data_access/UserRepository.java`
- `data_access/CompanyRepository.java`
- `data_access/PriceRepository.java`
- `data_access/PositionRepository.java`
- `data_access/TradeRepository.java`
- `data_access/PortfolioRepository.java`

### Supabase Implementation

- `data_access/SupabaseClient.java` - REST API wrapper ✅
- `data_access/AuthResponse.java` - Auth response model ✅
- `data_access/SupabasePortfolioRepository.java` - Reference implementation ✅
- `data_access/SupabaseUserRepository.java` - User profile management ✅
- `data_access/SupabaseCompanyRepository.java` - Company/stock metadata ✅
- `data_access/SupabasePriceRepository.java` - Historical price data ✅
- `data_access/SupabasePositionRepository.java` - Portfolio positions ✅
- `data_access/SupabaseTradeRepository.java` - Trade transactions ✅

### Existing Code (Still Works)

- `data_access/InMemoryPortfolioRepository.java` - Original in-memory implementation
- `use_case/track_portfolio/TrackPortfolioInteractor.java` - Existing use case
- `view/PortfolioPage.java`, `app/MainFrame.java` - Existing UI

---

## 🧪 Testing Strategy

### Unit Tests

- Test repositories with H2 in-memory database (for local testing)
- Mock SupabaseClient for testing use cases
- Test entity business logic (P/L calculations, etc.)

### Integration Tests

- Use Testcontainers with PostgreSQL for repository testing
- Test RLS policies with multiple user contexts
- Test transaction handling (trades should be atomic)

### Manual Testing Checklist

1. Sign up new user → verify user created in database
2. Login → verify JWT token received
3. Create portfolio → verify in database with correct user_id
4. Add trade → verify position and portfolio cash updated
5. View portfolio → verify P/L calculations correct
6. Delete portfolio → verify cascade deletes positions/trades
7. Try to access another user's portfolio → verify blocked by RLS

---

## 🚀 Deployment Checklist

### Prerequisites

- [ ] Java 11+ installed
- [ ] Maven 3.x installed
- [ ] Supabase account created

### Supabase Setup

- [ ] Create Supabase project
- [ ] Run `database/schema.sql` in SQL Editor
- [ ] Enable RLS on all tables (already in schema)
- [ ] Get SUPABASE_URL and SUPABASE_ANON_KEY
- [ ] Update `SupabaseClient.java` constants

### Application Setup

- [ ] Update `pom.xml` (already done)
- [ ] Compile: `mvn clean compile`
- [ ] Run tests: `mvn test`
- [ ] Package: `mvn package`
- [ ] Run: `java -jar target/TeamProject-1.0-SNAPSHOT.jar`

### Optional: Price Data Setup

- [ ] Get AlphaVantage API key
- [ ] Populate `companies` table with stock metadata
- [ ] Set up Supabase Edge Function for price updates
- [ ] Schedule function to run every 15 minutes

---

## 💡 Tips for Next Developer

1. **Start with authentication UI** - Get login/signup working first, then you can test everything else

2. **Use SupabasePortfolioRepository as template** - Copy the pattern for other repositories:

   ```java
   // Query pattern
   Entity[] results = client.queryWithFilter("table", "id=eq." + id, Entity[].class);

   // Insert pattern
   Entity[] inserted = client.insert("table", entity, Entity[].class);

   // Update pattern
   Entity[] updated = client.update("table", "id=eq." + id, entity, Entity[].class);

   // Delete pattern
   client.delete("table", "id=eq." + id);
   ```

3. **Test RLS policies in Supabase dashboard** - Use the Table Editor to verify policies work before coding

4. **Keep in-memory repo for now** - Don't delete `InMemoryPortfolioRepository` until Supabase is fully working

5. **Use dependency injection** - Pass repositories to use cases via constructor:

   ```java
   public TrackPortfolioInteractor(PortfolioRepository portfolioRepo,
                                   PriceRepository priceRepo) {
       this.portfolioRepo = portfolioRepo;
       this.priceRepo = priceRepo;
   }
   ```

6. **Handle errors gracefully** - Wrap IOException in RuntimeException with user-friendly messages

7. **Transaction handling** - For trades, you'll need to update 3 tables atomically:

   - Insert into `trades`
   - Update `portfolio_positions` (quantity, avg cost)
   - Update `portfolios` (current_cash)

   Consider using Supabase's transaction support or implement retry logic.

---

## 📞 Questions?

If you're stuck, check these resources:

1. `database/SERVERLESS_PLAN.md` - Complete Supabase guide with code examples
2. `database/IMPLEMENTATION_PLAN.md` - Repository interface specifications
3. Supabase docs: https://supabase.com/docs
4. PostgREST API docs: https://postgrest.org/en/stable/api.html

The foundation is solid. Just need to connect the dots! 🎯
