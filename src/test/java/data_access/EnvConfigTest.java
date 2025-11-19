package data_access;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data_access.config.EnvConfig;

/**
 * Tests for EnvConfig.
 * Note: These tests depend on the .env file being present.
 * In CI/CD, environment variables should be set through the environment.
 */
@DisplayName("Environment Configuration Tests")
public class EnvConfigTest {
    
    @Test
    @DisplayName("Should load Supabase URL")
    void testGetSupabaseUrl() {
        // Act
        String url = EnvConfig.getSupabaseUrl();
        
        // Assert
        assertNotNull(url);
        assertFalse(url.isEmpty());
        // Should be a URL (contains http)
        assertTrue(url.contains("http") || url.equals("https://your-project.supabase.co"));
    }
    
    @Test
    @DisplayName("Should load Supabase Anon Key")
    void testGetSupabaseAnonKey() {
        // Act
        String key = EnvConfig.getSupabaseAnonKey();
        
        // Assert
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }
    
    @Test
    @DisplayName("Should load Supabase Service Role Key")
    void testGetSupabaseServiceRoleKey() {
        // Act
        String key = EnvConfig.getSupabaseServiceRoleKey();
        
        // Assert
        assertNotNull(key);
        // May be empty if not configured
    }
    
    @Test
    @DisplayName("Should load Alpha Vantage API Key")
    void testGetAlphaVantageApiKey() {
        // Act
        String key = EnvConfig.getAlphaVantageApiKey();
        
        // Assert
        assertNotNull(key);
        // May be empty if not configured
    }
    
    @Test
    @DisplayName("Should load database URL")
    void testGetDatabaseUrl() {
        // Act
        String url = EnvConfig.getDatabaseUrl();
        
        // Assert
        assertNotNull(url);
        // May be empty if using Supabase REST API instead of direct DB connection
    }
    
    @Test
    @DisplayName("Should convert database URL to JDBC format")
    void testGetDbUrl() {
        // Act
        String jdbcUrl = EnvConfig.getDbUrl();
        
        // Assert
        assertNotNull(jdbcUrl);
        if (!jdbcUrl.isEmpty()) {
            // If there's a URL, it should be in JDBC format
            assertTrue(jdbcUrl.startsWith("jdbc:") || jdbcUrl.isEmpty());
        }
    }
    
    @Test
    @DisplayName("Should get application environment")
    void testGetAppEnv() {
        // Act
        String env = EnvConfig.getAppEnv();
        
        // Assert
        assertNotNull(env);
        assertFalse(env.isEmpty());
        // Should be one of the expected environments
        assertTrue(env.equals("development") || env.equals("production") || env.equals("test"));
    }
    
    @Test
    @DisplayName("Should check if environment is development")
    void testIsDevelopment() {
        // Act
        boolean isDev = EnvConfig.isDevelopment();
        
        // Assert
        // Should match the APP_ENV setting
        assertEquals("development".equalsIgnoreCase(EnvConfig.getAppEnv()), isDev);
    }
    
    @Test
    @DisplayName("Should check if environment is production")
    void testIsProduction() {
        // Act
        boolean isProd = EnvConfig.isProduction();
        
        // Assert
        // Should match the APP_ENV setting
        assertEquals("production".equalsIgnoreCase(EnvConfig.getAppEnv()), isProd);
    }
    
    @Test
    @DisplayName("Should not crash with missing .env file")
    void testMissingEnvFile() {
        // This test verifies that the ignoreIfMissing() configuration works
        // The class should not crash on initialization even if .env is missing
        // Act & Assert
        assertDoesNotThrow(() -> {
            String url = EnvConfig.getSupabaseUrl();
            assertNotNull(url);
        });
    }
    
    @Test
    @DisplayName("Should provide default values for missing variables")
    void testDefaultValues() {
        // Act
        String appEnv = EnvConfig.getAppEnv();
        String supabaseUrl = EnvConfig.getSupabaseUrl();
        
        // Assert - should have defaults even if .env is missing
        assertNotNull(appEnv);
        assertEquals("development", appEnv); // Default from code
        
        assertNotNull(supabaseUrl);
        // Will be either the actual URL or the default placeholder
    }
}
