package data_access;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Verifies the Supabase database schema and shows table information.
 */
public class VerifySchema {
    
    public static void main(String[] args) {
        System.out.println("=== Database Schema Verification ===\n");
        
        PostgresClient client = new PostgresClient();
        
        try (Connection conn = client.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("✓ Connected to: " + conn.getCatalog());
            System.out.println("  Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println();
            
            // List all tables in public schema
            String[] tables = {
                "user_profiles", "companies", "price_points", "candles",
                "portfolios", "portfolio_positions", "trades", "portfolio_snapshots"
            };
            
            System.out.println("📊 Table Overview:");
            System.out.println("─".repeat(80));
            
            try (Statement stmt = conn.createStatement()) {
                for (String table : tables) {
                    String query = String.format(
                        "SELECT COUNT(*) as row_count FROM public.%s", table
                    );
                    
                    try (ResultSet rs = stmt.executeQuery(query)) {
                        if (rs.next()) {
                            int count = rs.getInt("row_count");
                            System.out.printf("  %-25s | Rows: %d%n", table, count);
                        }
                    } catch (Exception e) {
                        System.out.printf("  %-25s | ERROR: %s%n", table, e.getMessage());
                    }
                }
            }
            
            System.out.println("─".repeat(80));
            System.out.println();
            
            // Check views
            System.out.println("📋 Views:");
            String[] views = {"portfolio_summary", "position_details", "trade_history"};
            
            for (String view : views) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeQuery("SELECT 1 FROM public." + view + " LIMIT 1");
                    System.out.println("  ✓ " + view);
                } catch (Exception e) {
                    System.out.println("  ✗ " + view + " (missing or error)");
                }
            }
            
            System.out.println("\n✅ Schema verification complete!");
            System.out.println("\nNext steps:");
            System.out.println("  1. Test CRUD operations with repository classes");
            System.out.println("  2. Populate companies table with stock data");
            System.out.println("  3. Create test user and portfolio");
            
        } catch (Exception e) {
            System.err.println("❌ Verification failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
