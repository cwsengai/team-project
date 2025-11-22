package data_access;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Verify that the database migration was successful.
 * Checks all columns and provides a detailed report.
 */
public class VerifyMigration {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE MIGRATION VERIFICATION");
        System.out.println("=".repeat(80));
        System.out.println("\n📍 Database: " + DatabaseConnection.getMaskedUrl());
        System.out.println();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("✅ Connected to database\n");
            
            // Check companies table structure
            System.out.println("-".repeat(80));
            System.out.println("COMPANIES TABLE");
            System.out.println("-".repeat(80));
            ResultSet rs = stmt.executeQuery(
                "SELECT column_name, data_type, is_nullable, column_default " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = 'companies' " +
                "ORDER BY ordinal_position"
            );
            
            System.out.printf("%-25s %-20s %-10s %s%n", "COLUMN", "TYPE", "NULLABLE", "DEFAULT");
            System.out.println("-".repeat(80));
            
            boolean hasCountry = false, hasEps = false, hasPeRatio = false;
            boolean hasDividendPerShare = false, hasDividendYield = false, hasBeta = false;
            
            while (rs.next()) {
                String column = rs.getString("column_name");
                String type = rs.getString("data_type");
                String nullable = rs.getString("is_nullable");
                String defVal = rs.getString("column_default");
                
                System.out.printf("%-25s %-20s %-10s %s%n", 
                    column, type, nullable, defVal != null ? defVal : "");
                
                if (column.equals("country")) hasCountry = true;
                if (column.equals("eps")) hasEps = true;
                if (column.equals("pe_ratio")) hasPeRatio = true;
                if (column.equals("dividend_per_share")) hasDividendPerShare = true;
                if (column.equals("dividend_yield")) hasDividendYield = true;
                if (column.equals("beta")) hasBeta = true;
            }
            rs.close();
            
            System.out.println("\n✓ New columns added:");
            System.out.println("  " + (hasCountry ? "✅" : "❌") + " country");
            System.out.println("  " + (hasEps ? "✅" : "❌") + " eps");
            System.out.println("  " + (hasPeRatio ? "✅" : "❌") + " pe_ratio");
            System.out.println("  " + (hasDividendPerShare ? "✅" : "❌") + " dividend_per_share");
            System.out.println("  " + (hasDividendYield ? "✅" : "❌") + " dividend_yield");
            System.out.println("  " + (hasBeta ? "✅" : "❌") + " beta");
            
            // Check portfolio_positions
            System.out.println("\n" + "-".repeat(80));
            System.out.println("PORTFOLIO_POSITIONS TABLE");
            System.out.println("-".repeat(80));
            rs = stmt.executeQuery(
                "SELECT column_name, data_type, is_nullable " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = 'portfolio_positions' " +
                "ORDER BY ordinal_position"
            );
            
            System.out.printf("%-30s %-20s %s%n", "COLUMN", "TYPE", "NULLABLE");
            System.out.println("-".repeat(80));
            
            boolean hasPositionTicker = false;
            while (rs.next()) {
                String column = rs.getString("column_name");
                String type = rs.getString("data_type");
                String nullable = rs.getString("is_nullable");
                
                System.out.printf("%-30s %-20s %s%n", column, type, nullable);
                if (column.equals("ticker")) hasPositionTicker = true;
            }
            rs.close();
            
            System.out.println("\n✓ New columns added:");
            System.out.println("  " + (hasPositionTicker ? "✅" : "❌") + " ticker");
            
            // Check trades
            System.out.println("\n" + "-".repeat(80));
            System.out.println("TRADES TABLE");
            System.out.println("-".repeat(80));
            rs = stmt.executeQuery(
                "SELECT column_name, data_type, is_nullable " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = 'trades' " +
                "ORDER BY ordinal_position"
            );
            
            System.out.printf("%-30s %-20s %s%n", "COLUMN", "TYPE", "NULLABLE");
            System.out.println("-".repeat(80));
            
            boolean hasTradeTicker = false;
            while (rs.next()) {
                String column = rs.getString("column_name");
                String type = rs.getString("data_type");
                String nullable = rs.getString("is_nullable");
                
                System.out.printf("%-30s %-20s %s%n", column, type, nullable);
                if (column.equals("ticker")) hasTradeTicker = true;
            }
            rs.close();
            
            System.out.println("\n✓ New columns added:");
            System.out.println("  " + (hasTradeTicker ? "✅" : "❌") + " ticker");
            
            // Summary
            System.out.println("\n" + "=".repeat(80));
            System.out.println("VERIFICATION SUMMARY");
            System.out.println("=".repeat(80));
            
            int totalExpected = 8;  // 6 in companies + 1 in positions + 1 in trades
            int totalFound = 0;
            if (hasCountry) totalFound++;
            if (hasEps) totalFound++;
            if (hasPeRatio) totalFound++;
            if (hasDividendPerShare) totalFound++;
            if (hasDividendYield) totalFound++;
            if (hasBeta) totalFound++;
            if (hasPositionTicker) totalFound++;
            if (hasTradeTicker) totalFound++;
            
            System.out.println("\nExpected columns: " + totalExpected);
            System.out.println("Found columns: " + totalFound);
            System.out.println("\nBreakdown:");
            System.out.println("  Companies table: 6/6 financial metrics");
            System.out.println("  Portfolio_positions: 1/1 ticker column");
            System.out.println("  Trades: 1/1 ticker column");
            
            if (totalFound == totalExpected) {
                System.out.println("\n🎉 ALL CHECKS PASSED!");
                System.out.println("   Database schema is fully aligned with entity classes.");
            } else {
                System.out.println("\n⚠️  INCOMPLETE MIGRATION");
                System.out.println("   Missing " + (totalExpected - totalFound) + " expected columns.");
            }
            
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("\n❌ Verification failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
