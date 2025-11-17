package data_access;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * One-time utility to fix the portfolio_positions trigger.
 * Replaces the incorrect update_updated_at_column trigger with
 * a proper update_last_updated_column trigger.
 */
public class ApplyPositionsTriggerFix {
    public static void main(String[] args) {
        try {
            PostgresClient client = new PostgresClient();
            
            System.out.println("Applying portfolio_positions trigger fix...\n");
            
            // Read the SQL file
            String sql = new String(Files.readAllBytes(Paths.get("database/fix_positions_trigger.sql")));
            
            // Execute the fix
            try (Connection conn = client.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Execute the entire SQL script
                stmt.execute(sql);
                
                System.out.println("✓ Dropped old trigger");
                System.out.println("✓ Created update_last_updated_column() function");
                System.out.println("✓ Created new trigger on portfolio_positions");
                System.out.println("\nTrigger fix applied successfully!");
            }
            
        } catch (IOException | SQLException e) {
            System.err.println("Error applying trigger fix: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
