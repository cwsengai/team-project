package data_access.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loads and provides access to environment variables from .env file.
 * Centralizes all configuration management for the application.
 */
public class EnvConfig {
    private static final Dotenv dotenv;

    static {
        dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
        // TODO: Add validation for required environment variables
        // TODO: Log warning if using default values in production
    }

    public static String getDatabaseUrl() {
        return dotenv.get("DATABASE_URL", "");
    }

    public static String getDbUrl() {
        String dbUrl = getDatabaseUrl();
        if (dbUrl.isEmpty()) {
            return "";
        }
        
        if (dbUrl.startsWith("postgresql://")) {
            return "jdbc:" + dbUrl;
        } else if (dbUrl.startsWith("jdbc:")) {
            return dbUrl;
        }
        
        return dbUrl;
    }

    public static String getSupabaseUrl() {
        return dotenv.get("SUPABASE_URL", "https://your-project.supabase.co");
    }

    public static String getSupabaseAnonKey() {
        return dotenv.get("SUPABASE_ANON_KEY", "your-anon-key-here");
    }

    public static String getSupabaseServiceRoleKey() {
        // TODO: Add security check to prevent exposing service role key in client-side code
        // TODO: Consider restricting access to this method to server-side components only
        return dotenv.get("SUPABASE_SERVICE_ROLE_KEY", "");
    }

    public static String getAlphaVantageApiKey() {
        return dotenv.get("ALPHA_VANTAGE_API_KEY", "");
    }

    public static String getAppEnv() {
        return dotenv.get("APP_ENV", "development");
    }

    public static boolean isDevelopment() {
        return "development".equalsIgnoreCase(getAppEnv());
    }

    public static boolean isProduction() {
        return "production".equalsIgnoreCase(getAppEnv());
    }
}
