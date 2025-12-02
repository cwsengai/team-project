package dataaccess;

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
            // Don't crash if .env doesn't exist (e.g., in production)
            .ignoreIfMissing()
            .load();
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
        return dotenv.get("ALPHA_VANTAGE_API_KEY", "demo");
    }

}
