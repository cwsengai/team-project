package data_access;

/*
 * COMMENTED OUT: This test suite uses PostgreSQL repositories which are being migrated to Supabase.
 * Will be deleted after Supabase test suites are implemented.
 
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import entity.Trade;
import entity.TradeType;


 Comprehensive test suite for PostgresTradeRepository.
 Tests all CRUD operations, queries, and trade immutability.
 
public class TradeRepositoryTestSuite {
    
    private static final PostgresTradeRepository tradeRepo = new PostgresTradeRepository();
    private static final PostgresPortfolioRepository portfolioRepo = new PostgresPortfolioRepository();
    private static final PostgresCompanyRepository companyRepo = new PostgresCompanyRepository();
    private static final PostgresClient client = new PostgresClient();
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    // Test IDs
    private static String testUserId;
    private static String testPortfolioId1;
    private static String testPortfolioId2;
    private static String testCompanyId1;
    private static String testCompanyId2;
    private static String testPositionId1;
    private static String testPositionId2;
    
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("|     Trade Repository Comprehensive Test Suite         |");
        System.out.println("==========================================================\n");
        
        try {
            // Setup
            System.out.println(" Setup: Creating test data and cleaning...");
            setupTestData();
            System.out.println("[OK] Setup complete\n");
            
            // Run all test categories
            testBasicOperations();
            cleanupTradeData();
            testPortfolioQueries();
            cleanupTradeData();
            testPositionQueries();
            cleanupTradeData();
            testDateRangeQueries();
            cleanupTradeData();
            testTradeTypes();
            cleanupTradeData();
            testEdgeCases();
            
            // Summary
            printSummary();
            
            // Cleanup
            System.out.println("\n Cleanup: Removing test data...");
            cleanupTestData();
            System.out.println("[OK] Cleanup complete");
            
            if (testsFailed > 0) {
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Test suite failed with exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void testBasicOperations() {
        System.out.println("=======================================================");
        System.out.println("  1. BASIC OPERATIONS");
        System.out.println("=======================================================\n");
        
        // Test 1.1: Save buy trade
        runTest("Save buy trade", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1,
                testPositionId1,
                testCompanyId1,
                "TRADE1",
                TradeType.BUY,
                100,
                50.0,
                1.99,
                LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            
            assertNotNull(saved, "Saved trade should not be null");
            assertEqualsStr(trade.getTicker(), saved.getTicker(), "Ticker should match");
            assertEqualsInt(100, saved.getQuantity(), "Quantity should match");
            assertEqualsDouble(50.0, saved.getPrice(), "Price should match");
        });
        
        // Test 1.2: Save sell trade
        runTest("Save sell trade", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1,
                testPositionId1,
                testCompanyId1,
                "TRADE1",
                TradeType.SELL,
                50,
                55.0,
                1.99,
                LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            
            assertNotNull(saved, "Saved trade should not be null");
            assertTrue(saved.getTradeType() == TradeType.SELL, "Should be SELL trade");
        });
        
        // Test 1.3: Save trade with no position ID (opening trade)
        runTest("Save trade without position ID", () -> {
            Trade trade = new Trade(
                UUID.randomUUID().toString(),
                testPortfolioId1,
                null,  // No position yet
                testCompanyId1,
                "TRADE1",
                TradeType.BUY,
                100,
                50.0,
                1.99,
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            assertNotNull(saved, "Trade should save without position ID");
        });
    }
    
    private static void testPortfolioQueries() {
        System.out.println("\n=======================================================");
        System.out.println("  2. PORTFOLIO QUERIES");
        System.out.println("=======================================================\n");
        
        // Test 2.1: Find trades by portfolio ID
        runTest("Find trades by portfolio ID", () -> {
            // Create multiple trades for same portfolio
            for (int i = 0; i < 5; i++) {
                Trade trade = createTestTrade(
                    testPortfolioId1,
                    testPositionId1,
                    testCompanyId1,
                    "TRADE1",
                    i % 2 == 0 ? TradeType.BUY : TradeType.SELL,
                    100,
                    50.0 + i,
                    1.99,
                    LocalDateTime.now().minusDays(i)
                );
                tradeRepo.save(trade);
            }
            
            List<Trade> trades = tradeRepo.findByPortfolioId(testPortfolioId1);
            
            assertTrue(trades.size() >= 5, "Should find at least 5 trades");
            // Verify ordered by executed_at DESC (newest first)
            for (int i = 1; i < trades.size(); i++) {
                assertTrue(
                    !trades.get(i).getExecutedAt().isAfter(trades.get(i - 1).getExecutedAt()),
                    "Trades should be ordered newest first"
                );
            }
        });
        
        // Test 2.2: Portfolio isolation
        runTest("Portfolio isolation", () -> {
            // Create trades for two different portfolios
            tradeRepo.save(createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 100, 50.0, 1.99, LocalDateTime.now()
            ));
            tradeRepo.save(createTestTrade(
                testPortfolioId2, testPositionId2, testCompanyId2, "TRADE2",
                TradeType.BUY, 200, 25.0, 1.99, LocalDateTime.now()
            ));
            
            List<Trade> portfolio1Trades = tradeRepo.findByPortfolioId(testPortfolioId1);
            List<Trade> portfolio2Trades = tradeRepo.findByPortfolioId(testPortfolioId2);
            
            // Verify isolation
            for (Trade t : portfolio1Trades) {
                assertEqualsStr(testPortfolioId1, t.getPortfolioId(), "Should only return portfolio 1 trades");
            }
            for (Trade t : portfolio2Trades) {
                assertEqualsStr(testPortfolioId2, t.getPortfolioId(), "Should only return portfolio 2 trades");
            }
        });
        
        // Test 2.3: Empty portfolio
        runTest("Find trades - empty portfolio", () -> {
            String emptyPortfolioId = UUID.randomUUID().toString();
            List<Trade> trades = tradeRepo.findByPortfolioId(emptyPortfolioId);
            
            assertEqualsInt(0, trades.size(), "Should return empty list for portfolio with no trades");
        });
    }
    
    private static void testPositionQueries() {
        System.out.println("\n=======================================================");
        System.out.println("  3. POSITION QUERIES");
        System.out.println("=======================================================\n");
        
        // Test 3.1: Find trades by position ID
        runTest("Find trades by position ID", () -> {
            // Create sequence of trades for same position
            LocalDateTime base = LocalDateTime.now().minusDays(10);
            for (int i = 0; i < 4; i++) {
                Trade trade = createTestTrade(
                    testPortfolioId1,
                    testPositionId1,
                    testCompanyId1,
                    "TRADE1",
                    i == 0 || i == 1 ? TradeType.BUY : TradeType.SELL,
                    100,
                    50.0,
                    1.99,
                    base.plusDays(i)
                );
                tradeRepo.save(trade);
            }
            
            List<Trade> trades = tradeRepo.findByPositionId(testPositionId1);
            
            assertTrue(trades.size() >= 4, "Should find at least 4 trades for position");
            // Verify ordered by executed_at ASC (chronological)
            for (int i = 1; i < trades.size(); i++) {
                assertTrue(
                    !trades.get(i).getExecutedAt().isBefore(trades.get(i - 1).getExecutedAt()),
                    "Trades should be in chronological order"
                );
            }
        });
        
        // Test 3.2: Position with no trades
        runTest("Find trades - position with no trades", () -> {
            String emptyPositionId = UUID.randomUUID().toString();
            List<Trade> trades = tradeRepo.findByPositionId(emptyPositionId);
            
            assertEqualsInt(0, trades.size(), "Should return empty list for position with no trades");
        });
    }
    
    private static void testDateRangeQueries() {
        System.out.println("\n=======================================================");
        System.out.println("  4. DATE RANGE QUERIES");
        System.out.println("=======================================================\n");
        
        // Test 4.1: Find trades in date range
        runTest("Find trades in date range", () -> {
            LocalDateTime base = LocalDateTime.now().minusDays(100).withHour(12).withMinute(0).withSecond(0).withNano(0);
            
            // Create trades across 10 days
            for (int i = 0; i < 10; i++) {
                Trade trade = createTestTrade(
                    testPortfolioId1,
                    testPositionId1,
                    testCompanyId1,
                    "TRADE1",
                    TradeType.BUY,
                    100 + i,  // Vary quantity to make them unique
                    50.0,
                    1.99,
                    base.plusDays(i)
                );
                tradeRepo.save(trade);
            }
            
            // Query for middle range (days 3-7 inclusive = 5 trades, avoiding boundary issues)
            LocalDateTime start = base.plusDays(3);
            LocalDateTime end = base.plusDays(7);
            
            List<Trade> trades = tradeRepo.findByPortfolioInDateRange(testPortfolioId1, start, end);
            
            assertEqualsInt(5, trades.size(), "Should find exactly 5 trades in range");
            // Verify all trades are within range
            for (Trade trade : trades) {
                assertTrue(
                    !trade.getExecutedAt().isBefore(start) && !trade.getExecutedAt().isAfter(end),
                    "Trade should be within date range (start: " + start + ", end: " + end + ", found: " + trade.getExecutedAt() + ")"
                );
            }
        });
        
        // Test 4.2: Empty date range
        runTest("Find trades - no trades in range", () -> {
            LocalDateTime start = LocalDateTime.now().plusDays(100);
            LocalDateTime end = LocalDateTime.now().plusDays(200);
            
            List<Trade> trades = tradeRepo.findByPortfolioInDateRange(testPortfolioId1, start, end);
            
            assertEqualsInt(0, trades.size(), "Should return empty list for future date range");
        });
        
        // Test 4.3: Single day range
        runTest("Find trades - single day", () -> {
            LocalDateTime day = LocalDateTime.now().minusDays(50).withHour(12).withMinute(0);
            
            Trade trade = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 100, 50.0, 1.99, day
            );
            tradeRepo.save(trade);
            
            List<Trade> trades = tradeRepo.findByPortfolioInDateRange(
                testPortfolioId1,
                day.minusHours(1),
                day.plusHours(1)
            );
            
            assertTrue(trades.size() >= 1, "Should find trade on specific day");
        });
    }
    
    private static void testTradeTypes() {
        System.out.println("\n=======================================================");
        System.out.println("  5. TRADE TYPES");
        System.out.println("=======================================================\n");
        
        // Test 5.1: Buy trade calculations
        runTest("Buy trade - total amount includes fees", () -> {
            Trade buy = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 100, 50.0, 5.0, LocalDateTime.now()
            );
            
            assertEqualsDouble(5000.0, buy.getTradeValue(), "Trade value should be quantity * price");
            assertEqualsDouble(5005.0, buy.getTotalAmount(), "Total should include fees for buy");
        });
        
        // Test 5.2: Sell trade calculations
        runTest("Sell trade - total amount deducts fees", () -> {
            Trade sell = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.SELL, 100, 50.0, 5.0, LocalDateTime.now()
            );
            
            assertEqualsDouble(5000.0, sell.getTradeValue(), "Trade value should be quantity * price");
            assertEqualsDouble(4995.0, sell.getTotalAmount(), "Total should deduct fees for sell");
        });
        
        // Test 5.3: Zero fees
        runTest("Trade with zero fees", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 100, 50.0, 0.0, LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            assertEqualsDouble(0.0, saved.getFees(), "Fees should be zero");
        });
    }
    
    private static void testEdgeCases() {
        System.out.println("\n=======================================================");
        System.out.println("  6. EDGE CASES");
        System.out.println("=======================================================\n");
        
        // Test 6.1: Large quantity
        runTest("Handle large quantity", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 1000000, 0.01, 10.0, LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            assertEqualsInt(1000000, saved.getQuantity(), "Large quantity should be preserved");
        });
        
        // Test 6.2: Fractional price
        runTest("Handle fractional penny prices", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 100, 0.0001, 0.01, LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            assertTrue(saved.getPrice() < 0.001, "Fractional price should be preserved");
        });
        
        // Test 6.3: Very high price
        runTest("Handle very high stock price", () -> {
            Trade trade = createTestTrade(
                testPortfolioId1, testPositionId1, testCompanyId1, "TRADE1",
                TradeType.BUY, 1, 100000.0, 50.0, LocalDateTime.now()
            );
            
            Trade saved = tradeRepo.save(trade);
            assertEqualsDouble(100000.0, saved.getPrice(), "High price should be preserved");
        });
    }
    
    // ===== Test Utilities =====
    
    private static Trade createTestTrade(String portfolioId, String positionId, String companyId,
                                          String ticker, TradeType type, int quantity,
                                          double price, double fees, LocalDateTime executedAt) {
        return new Trade(
            UUID.randomUUID().toString(),
            portfolioId,
            positionId,
            companyId,
            ticker,
            type,
            quantity,
            price,
            fees,
            executedAt,
            LocalDateTime.now()
        );
    }
    
    private static void setupTestData() {
        cleanupTestData();
        
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Disable FK constraint for testing
            stmt.execute("ALTER TABLE public.trades DROP CONSTRAINT IF EXISTS trades_portfolio_id_fkey");
            stmt.execute("ALTER TABLE public.trades DROP CONSTRAINT IF EXISTS trades_position_id_fkey");
            stmt.execute("ALTER TABLE public.trades DROP CONSTRAINT IF EXISTS trades_company_id_fkey");
            System.out.println("Note: FK constraints temporarily disabled for testing");
        } catch (Exception e) {
            System.err.println("Warning: Failed to disable constraints - " + e.getMessage());
        }
        
        // Create test IDs
        testUserId = UUID.randomUUID().toString();
        testPortfolioId1 = UUID.randomUUID().toString();
        testPortfolioId2 = UUID.randomUUID().toString();
        testCompanyId1 = UUID.randomUUID().toString();
        testCompanyId2 = UUID.randomUUID().toString();
        testPositionId1 = UUID.randomUUID().toString();
        testPositionId2 = UUID.randomUUID().toString();
    }
    
    private static void cleanupTestData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete all test trades (UUID pattern indicates test data)
            stmt.execute("DELETE FROM public.trades");
        } catch (Exception e) {
            System.err.println("Warning: Cleanup failed - " + e.getMessage());
        }
    }
    
    private static void cleanupTradeData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete all test trades (more aggressive cleanup)
            stmt.execute("DELETE FROM public.trades WHERE portfolio_id::text LIKE '%-%-%-%-%'");
        } catch (Exception e) {
            System.err.println("Warning: Trade cleanup failed - " + e.getMessage());
        }
    }
    
    private static void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("[OK] " + testName);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("[FAIL] " + testName);
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("[FAIL] " + testName);
            System.out.println("  Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
    
    private static void assertEqualsStr(String expected, String actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }
    
    private static void assertEqualsDouble(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.01) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }
    
    private static void assertEqualsInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }
    
    private static void printSummary() {
        System.out.println("\n==========================================================");
        System.out.println("|                    TEST SUMMARY                        |");
        System.out.println("==========================================================");
        System.out.printf("|  Total Tests:  %-40d|%n", testsRun);
        System.out.printf("|  Passed:       %-40d|%n", testsPassed);
        System.out.printf("|  Failed:       %-40d|%n", testsFailed);
        System.out.printf("|  Success Rate: %-39.1f%%|%n", (testsRun > 0 ? (testsPassed * 100.0 / testsRun) : 0));
        System.out.println("==========================================================");
        
        if (testsFailed == 0) {
            System.out.println("\n*** All tests passed!");
        } else {
            System.out.println("\nWARNING:  Some tests failed. Please review the output above.");
        }
    }
}
*/
