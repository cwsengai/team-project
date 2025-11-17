package data_access;

import java.sql.Connection;
import java.sql.Statement;

/**
 * One-time utility to create the users table from schema.sql.
 * Extracts and executes only the users table creation portion.
 */
public class CreateUsersTable {
    public static void main(String[] args) {
        try {
            PostgresClient client = new PostgresClient();
            
            System.out.println("Creating users table...\n");
            
            // Create users table SQL
            String sql = "CREATE TABLE IF NOT EXISTS users (\n" +
                        "  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),\n" +
                        "  email text NOT NULL UNIQUE,\n" +
                        "  password_hash text NOT NULL,\n" +
                        "  display_name text,\n" +
                        "  created_at timestamptz DEFAULT now(),\n" +
                        "  last_login timestamptz\n" +
                        ");\n" +
                        "\n" +
                        "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);";
            
            try (Connection conn = client.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute(sql);
                
                System.out.println("✓ Created users table");
                System.out.println("✓ Created index on email");
                System.out.println("\nUsers table created successfully!");
            }
            
        } catch (Exception e) {
            System.err.println("Error creating users table: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
