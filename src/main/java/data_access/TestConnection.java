package data_access;

import java.sql.Connection;

/**
 * Simple test to verify database connection configuration.
 */
public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===\n");
        
        try {
            // Test connection string parsing
            String rawUrl = EnvConfig.getDatabaseUrl();
            System.out.println("✓ Raw DATABASE_URL loaded from .env");
            System.out.println("  Format check: " + (rawUrl.startsWith("postgresql://") ? "OK" : "INVALID"));
            
            String jdbcUrl = EnvConfig.getDbUrl();
            System.out.println("\n✓ Converted to JDBC URL");
            System.out.println("  JDBC URL: " + jdbcUrl);
            System.out.println("  Format check: " + (jdbcUrl.startsWith("jdbc:postgresql://") ? "OK" : "INVALID"));
            
            // Show URL structure (with password masked)
            String maskedUrl = jdbcUrl.replaceAll(":[^:@]+@", ":****@");
            System.out.println("  Masked URL: " + maskedUrl);
            
            // Test actual connection
            System.out.println("\n⏳ Attempting to connect to database...");
            PostgresClient client = new PostgresClient();
            
            try (Connection conn = client.getConnection()) {
                System.out.println("✓ Connection successful!");
                System.out.println("  Connected to: " + conn.getCatalog());
                System.out.println("  Database product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("  Database version: " + conn.getMetaData().getDatabaseProductVersion());
                
                System.out.println("\n✅ All tests passed! Database is ready.");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Connection failed!");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            
            System.err.println("\nFull stack trace:");
            e.printStackTrace();
            
            System.err.println("\nPossible issues:");
            System.err.println("  1. DATABASE_URL password is incorrect");
            System.err.println("  2. DATABASE_URL format is wrong (should be: postgresql://user:pass@host:port/db)");
            System.err.println("  3. Network/firewall blocking connection to Supabase");
            System.err.println("  4. Supabase project is paused/inactive");
            System.err.println("  5. Connection pooler settings (try direct connection or transaction mode)");
            System.exit(1);
        }
    }
}
