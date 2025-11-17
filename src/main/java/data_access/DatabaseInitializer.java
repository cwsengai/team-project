package data_access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to initialize the database schema.
 * Reads and executes the schema.sql file.
 */
public class DatabaseInitializer {
    
    private final PostgresClient postgresClient;

    public DatabaseInitializer(PostgresClient postgresClient) {
        this.postgresClient = postgresClient;
    }

    /**
     * Execute the schema.sql file to create all tables, indexes, and triggers.
     *
     * @param schemaFilePath absolute path to the schema.sql file
     * @throws SQLException if database operations fail
     * @throws IOException if file reading fails
     */
    public void initializeSchema(String schemaFilePath) throws SQLException, IOException {
        System.out.println("Reading schema from: " + schemaFilePath);
        String sql = readSqlFile(schemaFilePath);
        
        System.out.println("Executing schema SQL...");
        executeSQL(sql);
        
        System.out.println("✅ Database schema initialized successfully!");
    }

    /**
     * Read the entire SQL file as a string.
     */
    private String readSqlFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }

    /**
     * Execute SQL statements.
     * Splits on semicolon and executes each statement separately.
     */
    private void executeSQL(String sql) throws SQLException {
        try (Connection conn = postgresClient.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Split SQL into individual statements (basic splitting on semicolons)
            String[] statements = sql.split(";");
            
            for (String statement : statements) {
                String trimmed = statement.trim();
                
                // Skip empty statements and comments
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                
                try {
                    System.out.println("Executing: " + 
                        (trimmed.length() > 50 ? trimmed.substring(0, 50) + "..." : trimmed));
                    stmt.execute(trimmed);
                } catch (SQLException e) {
                    System.err.println("Error executing statement: " + trimmed.substring(0, Math.min(100, trimmed.length())));
                    throw e;
                }
            }
        }
    }

    /**
     * Test the database connection.
     */
    public boolean testConnection() {
        return postgresClient.testConnection();
    }

    /**
     * Main method to run schema initialization from command line.
     */
    public static void main(String[] args) {
        try {
            // Create PostgreSQL client using .env configuration
            PostgresClient postgresClient = new PostgresClient();
            DatabaseInitializer initializer = new DatabaseInitializer(postgresClient);
            
            // Test connection first
            System.out.println("Testing database connection...");
            if (!initializer.testConnection()) {
                System.err.println("❌ Failed to connect to database!");
                System.err.println("Check your .env file settings:");
                System.err.println("  DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD");
                System.exit(1);
            }
            System.out.println("✅ Database connection successful!");
            
            // Get schema file path
            String schemaPath = "database/schema.sql";
            if (args.length > 0) {
                schemaPath = args[0];
            }
            
            // Initialize schema
            initializer.initializeSchema(schemaPath);
            
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("❌ File Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
