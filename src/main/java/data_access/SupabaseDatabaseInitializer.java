package data_access;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes the Supabase database by executing the schema SQL script.
 * Uses JDBC to connect directly to Supabase PostgreSQL and run the SQL.
 */
public class SupabaseDatabaseInitializer {
    
    private final PostgresClient client;
    
    public SupabaseDatabaseInitializer() {
        this.client = new PostgresClient();
    }
    
    /**
     * Reads and executes the Supabase schema SQL file.
     */
    public void initializeSchema() throws Exception {
        System.out.println("=== Supabase Database Initialization ===");
        
        // Read the schema file
        String schemaPath = "database/supabase_schema.sql";
        System.out.println("Reading schema from: " + schemaPath);
        
        String fullSql = new String(Files.readAllBytes(Paths.get(schemaPath)));
        
        System.out.println("Connecting to Supabase PostgreSQL...");
        
        try (Connection conn = client.getConnection()) {
            System.out.println("Connected successfully!");
            System.out.println("Executing schema...");
            
            // Split SQL into individual statements (separated by semicolons)
            String[] statements = fullSql.split(";");
            int successCount = 0;
            int errorCount = 0;
            
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue; // Skip empty or comment-only statements
                    }
                    
                    try {
                        stmt.execute(trimmed);
                        successCount++;
                        System.out.print(".");
                    } catch (Exception e) {
                        // Some errors are expected (e.g., "table already exists")
                        if (e.getMessage().contains("already exists") || 
                            e.getMessage().contains("does not exist")) {
                            System.out.print("s"); // skip
                        } else {
                            System.err.println("\n[ERROR] " + e.getMessage());
                            System.err.println("Statement: " + trimmed.substring(0, Math.min(100, trimmed.length())));
                            errorCount++;
                        }
                    }
                }
            }
            
            System.out.println("\n\n=== Results ===");
            System.out.println("Statements executed: " + successCount);
            System.out.println("Errors encountered: " + errorCount);
            System.out.println("Schema initialization complete!");
            
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("1. DATABASE_URL in .env has the correct password");
            System.err.println("2. Supabase project is active");
            System.err.println("3. Network connection is working");
            throw e;
        }
    }
    
    public static void main(String[] args) {
        try {
            SupabaseDatabaseInitializer initializer = new SupabaseDatabaseInitializer();
            initializer.initializeSchema();
            System.out.println("\nDatabase is ready to use!");
        } catch (Exception e) {
            System.err.println("\nInitialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
