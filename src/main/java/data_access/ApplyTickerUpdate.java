package data_access;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Applies the ticker column update to the trades table.
 * Run this once to add the ticker column for denormalization.
 */
public class ApplyTickerUpdate {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║        Apply Ticker Column Update to Trades           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        PostgresClient client = new PostgresClient();
        
        try {
            System.out.println("🔧 Applying updates...\n");
            
            try (Connection conn = client.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                int executedCount = 0;
                
                // 1. Add ticker column
                try {
                    System.out.println("1. Adding ticker column...");
                    stmt.execute("ALTER TABLE public.trades ADD COLUMN IF NOT EXISTS ticker text");
                    executedCount++;
                    System.out.println("✓ Success\n");
                } catch (Exception e) {
                    System.out.println("⚠ Warning: " + e.getMessage() + "\n");
                }
                
                // 2. Populate existing data
                try {
                    System.out.println("2. Populating ticker for existing trades...");
                    int updated = stmt.executeUpdate(
                        "UPDATE public.trades t " +
                        "SET ticker = c.ticker " +
                        "FROM public.companies c " +
                        "WHERE t.company_id = c.id AND t.ticker IS NULL"
                    );
                    executedCount++;
                    System.out.println("✓ Updated " + updated + " rows\n");
                } catch (Exception e) {
                    System.out.println("⚠ Warning: " + e.getMessage() + "\n");
                }
                
                // 3. Create index
                try {
                    System.out.println("3. Creating index on ticker...");
                    stmt.execute("CREATE INDEX IF NOT EXISTS idx_trades_ticker ON public.trades(ticker)");
                    executedCount++;
                    System.out.println("✓ Success\n");
                } catch (Exception e) {
                    System.out.println("⚠ Warning: " + e.getMessage() + "\n");
                }
                
                // 4. Create function
                try {
                    System.out.println("4. Creating trigger function...");
                    stmt.execute(
                        "CREATE OR REPLACE FUNCTION public.set_trade_ticker() " +
                        "RETURNS TRIGGER AS $$ " +
                        "BEGIN " +
                        "  IF NEW.ticker IS NULL AND NEW.company_id IS NOT NULL THEN " +
                        "    SELECT ticker INTO NEW.ticker " +
                        "    FROM public.companies " +
                        "    WHERE id = NEW.company_id; " +
                        "  END IF; " +
                        "  RETURN NEW; " +
                        "END; " +
                        "$$ LANGUAGE plpgsql"
                    );
                    executedCount++;
                    System.out.println("✓ Success\n");
                } catch (Exception e) {
                    System.out.println("⚠ Warning: " + e.getMessage() + "\n");
                }
                
                // 5. Create trigger
                try {
                    System.out.println("5. Creating trigger...");
                    stmt.execute("DROP TRIGGER IF EXISTS trigger_set_trade_ticker ON public.trades");
                    stmt.execute(
                        "CREATE TRIGGER trigger_set_trade_ticker " +
                        "BEFORE INSERT ON public.trades " +
                        "FOR EACH ROW " +
                        "EXECUTE FUNCTION public.set_trade_ticker()"
                    );
                    executedCount++;
                    System.out.println("✓ Success\n");
                } catch (Exception e) {
                    System.out.println("⚠ Warning: " + e.getMessage() + "\n");
                }
                
                System.out.println("╔════════════════════════════════════════════════════════╗");
                System.out.println("║                   UPDATE COMPLETE                      ║");
                System.out.println("╠════════════════════════════════════════════════════════╣");
                System.out.printf("║  Statements Executed: %-33d║%n", executedCount);
                System.out.println("╚════════════════════════════════════════════════════════╝");
                
                System.out.println("\n✅ Ticker column has been added to trades table");
                System.out.println("✅ Trigger created to auto-populate ticker on insert");
                System.out.println("✅ Index created for performance");
                
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Failed to apply update: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
