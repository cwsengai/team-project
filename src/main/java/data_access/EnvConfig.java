package data_access;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loads and provides access to environment variables from .env file.
 * Centralizes all configuration management for the application.
 */
public class EnvConfig {
    private static final Dotenv dotenv;

    static {
        // Load .env file from project root
        dotenv = Dotenv.configure()
            .ignoreIfMissing() // Don't crash if .env doesn't exist (e.g., in production)
            .load();
    }

    // Database Configuration (PostgreSQL)
    public static String getDbHost() {
        return dotenv.get("DB_HOST", "localhost");
    }

    public static int getDbPort() {
        return Integer.parseInt(dotenv.get("DB_PORT", "5432"));
    }

    public static String getDbName() {
        return dotenv.get("DB_NAME", "portfolio_tracker");
    }

    public static String getDbUser() {
        return dotenv.get("DB_USER", "postgres");
    }

    public static String getDbPassword() {
        return dotenv.get("DB_PASSWORD", "");
    }

    public static String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s",
            getDbHost(), getDbPort(), getDbName());
    }

    // Supabase Configuration
    public static String getSupabaseUrl() {
        return dotenv.get("SUPABASE_URL", "https://your-project.supabase.co");
    }

    public static String getSupabaseAnonKey() {
        return dotenv.get("SUPABASE_ANON_KEY", "your-anon-key-here");
    }

    public static String getSupabaseServiceRoleKey() {
        return dotenv.get("SUPABASE_SERVICE_ROLE_KEY", "");
    }

    // API Keys
    public static String getAlphaVantageApiKey() {
        return dotenv.get("ALPHA_VANTAGE_API_KEY", "");
    }

    // Application Settings
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
