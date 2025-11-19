package data_access;

/*
 * COMMENTED OUT: This test suite uses PostgreSQL repositories which are being migrated to Supabase.
 * Will be deleted after Supabase test suites are implemented.
 
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entity.Company;
import entity.PricePoint;
import entity.TimeInterval;


 Comprehensive test suite for PostgresPriceRepository.
 Tests all CRUD operations, queries, and edge cases for price data.
 
public class PriceRepositoryTestSuite {
    
    private static final PostgresPriceRepository priceRepo = new PostgresPriceRepository();
    private static final PostgresCompanyRepository companyRepo = new PostgresCompanyRepository();
    private static final PostgresClient client = new PostgresClient();
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    // Test company IDs
    private static String testCompanyId1;
    private static String testCompanyId2;
    
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("|    Price Repository Comprehensive Test Suite          |");
        System.out.println("==========================================================\n");
        
        try {
            // Setup
            System.out.println(" Setup: Creating test companies and cleaning data...");
            setupTestData();
            System.out.println("[OK] Setup complete\n");
            
            // Run all test categories
            testBasicCRUD();
            cleanupPriceData();
            testLatestPriceQueries();
            cleanupPriceData();
            testHistoricalQueries();
            cleanupPriceData();
            testBulkOperations();
            cleanupPriceData();
            testEdgeCases();
            cleanupPriceData();
            testDataIntegrity();
            cleanupPriceData();
            testCleanup();
            
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
    
    private static void testBasicCRUD() {
        System.out.println("=======================================================");
        System.out.println("  1. BASIC CRUD OPERATIONS");
        System.out.println("=======================================================\n");
        
        // Test 1.1: Save single price point
        runTest("Save single price point", () -> {
            PricePoint price = createTestPrice(
                testCompanyId1,
                LocalDateTime.now().minusDays(1),
                TimeInterval.DAILY,
                100.0, 105.0, 99.0, 103.0, 1000000.0
            );
            
            priceRepo.savePricePoint(price);
            
            Optional<PricePoint> found = priceRepo.getLatestPrice("PRICE1", TimeInterval.DAILY);
            assertTrue(found.isPresent(), "Price should be saved and retrievable");
            assertEqualsDouble(103.0, found.get().getClose(), "Close price should match");
        });
        
        // Test 1.2: Save price with null values
        runTest("Save price with null OHLV values", () -> {
            PricePoint price = new PricePoint(
                testCompanyId1,  // companySymbol
                LocalDateTime.now().minusHours(12),
                TimeInterval.INTRADAY,
                null, null, null,  // null open, high, low
                50.0,              // only close
                null,              // null volume
                "test"
            );
            
            priceRepo.savePricePoint(price);
            // Should not throw exception
        });
        
        // Test 1.3: Update existing price (upsert)
        runTest("Update existing price via upsert", () -> {
            LocalDateTime timestamp = LocalDateTime.now().minusDays(2);
            
            PricePoint original = createTestPrice(
                testCompanyId1, timestamp, TimeInterval.DAILY,
                200.0, 205.0, 199.0, 202.0, 2000000.0
            );
            priceRepo.savePricePoint(original);
            
            // Update with same timestamp (should trigger upsert)
            PricePoint updated = createTestPrice(
                testCompanyId1, timestamp, TimeInterval.DAILY,
                200.0, 210.0, 198.0, 208.0, 2500000.0
            );
            priceRepo.savePricePoint(updated);
            
            // Verify update
            List<PricePoint> history = priceRepo.getHistoricalPrices(
                "PRICE1",
                timestamp.minusMinutes(1),
                timestamp.plusMinutes(1),
                TimeInterval.DAILY
            );
            
            assertEqualsInt(1, history.size(), "Should only have one price point at this timestamp");
            assertEqualsDouble(208.0, history.get(0).getClose(), "Close should be updated");
            assertEqualsDouble(210.0, history.get(0).getHigh(), "High should be updated");
        });
    }
    
    private static void testLatestPriceQueries() {
        System.out.println("\n=======================================================");
        System.out.println("  2. LATEST PRICE QUERIES");
        System.out.println("=======================================================\n");
        
        // Test 2.1: Get latest price - finds most recent
        runTest("Get latest price - returns most recent", () -> {
            // Setup: Create price history for this specific test
            LocalDateTime base = LocalDateTime.now().minusDays(30);
            for (int i = 0; i < 5; i++) {
                priceRepo.savePricePoint(createTestPrice(
                    testCompanyId1,
                    base.plusDays(i),
                    TimeInterval.DAILY,
                    100.0 + i, 105.0 + i, 99.0 + i, 103.0 + i, 1000000.0
                ));
            }
            
            Optional<PricePoint> latest = priceRepo.getLatestPrice("PRICE1", TimeInterval.DAILY);
            assertTrue(latest.isPresent(), "Should find latest price");
            assertEqualsDouble(107.0, latest.get().getClose(), "Should return most recent close price");
        });
        
        // Test 2.2: Get latest price - different intervals
        runTest("Get latest price - interval filtering", () -> {
            // Add intraday price
            priceRepo.savePricePoint(createTestPrice(
                testCompanyId1,
                LocalDateTime.now().minusHours(1),
                TimeInterval.INTRADAY,
                150.0, 155.0, 149.0, 153.0, 500000.0
            ));
            
            Optional<PricePoint> daily = priceRepo.getLatestPrice("PRICE1", TimeInterval.DAILY);
            Optional<PricePoint> intraday = priceRepo.getLatestPrice("PRICE1", TimeInterval.INTRADAY);
            
            assertTrue(daily.isPresent(), "Should find daily price");
            assertTrue(intraday.isPresent(), "Should find intraday price");
            assertEqualsDouble(153.0, intraday.get().getClose(), "Intraday close should be 153");
        });
        
        // Test 2.3: Get latest price - no data
        runTest("Get latest price - ticker not found", () -> {
            Optional<PricePoint> notFound = priceRepo.getLatestPrice("NONEXISTENT", TimeInterval.DAILY);
            assertFalse(notFound.isPresent(), "Should not find price for non-existent ticker");
        });
        
        // Test 2.4: Get latest prices - multiple tickers
        runTest("Get latest prices - bulk query", () -> {
            // Add price for second company
            priceRepo.savePricePoint(createTestPrice(
                testCompanyId2,
                LocalDateTime.now().minusDays(1),
                TimeInterval.DAILY,
                50.0, 52.0, 49.0, 51.0, 800000.0
            ));
            
            Map<String, PricePoint> prices = priceRepo.getLatestPrices(
                List.of("PRICE1", "PRICE2")
            );
            
            assertEqualsInt(2, prices.size(), "Should find prices for both tickers");
            assertTrue(prices.containsKey("PRICE1"), "Should contain PRICE1");
            assertTrue(prices.containsKey("PRICE2"), "Should contain PRICE2");
            assertEqualsDouble(51.0, prices.get("PRICE2").getClose(), "PRICE2 close should be 51");
        });
        
        // Test 2.5: Get latest prices - empty list
        runTest("Get latest prices - empty ticker list", () -> {
            Map<String, PricePoint> prices = priceRepo.getLatestPrices(List.of());
            assertEqualsInt(0, prices.size(), "Should return empty map for empty input");
        });
    }
    
    private static void testHistoricalQueries() {
        System.out.println("\n=======================================================");
        System.out.println("  3. HISTORICAL PRICE QUERIES");
        System.out.println("=======================================================\n");
        
        // Test 3.1: Get historical prices - date range
        runTest("Get historical prices - date range", () -> {
            // Setup: Create test data
            LocalDateTime base = LocalDateTime.now().minusDays(40);
            for (int i = 0; i < 5; i++) {
                priceRepo.savePricePoint(createTestPrice(
                    testCompanyId1,
                    base.plusDays(i),
                    TimeInterval.DAILY,
                    100.0 + i, 105.0 + i, 99.0 + i, 103.0 + i, 1000000.0
                ));
            }
            
            LocalDateTime start = base;
            LocalDateTime end = base.plusDays(4);
            
            List<PricePoint> history = priceRepo.getHistoricalPrices(
                "PRICE1", start, end, TimeInterval.DAILY
            );
            
            assertTrue(history.size() >= 2, "Should find prices in range");
            // Verify ascending order
            for (int i = 1; i < history.size(); i++) {
                assertTrue(
                    history.get(i).getTimestamp().isAfter(history.get(i - 1).getTimestamp()),
                    "Prices should be in ascending timestamp order"
                );
            }
        });
        
        // Test 3.2: Get historical prices - empty range
        runTest("Get historical prices - no data in range", () -> {
            LocalDateTime start = LocalDateTime.now().plusDays(10);
            LocalDateTime end = LocalDateTime.now().plusDays(20);
            
            List<PricePoint> history = priceRepo.getHistoricalPrices(
                "PRICE1", start, end, TimeInterval.DAILY
            );
            
            assertEqualsInt(0, history.size(), "Should return empty list for future dates");
        });
        
        // Test 3.3: Get historical prices - single day
        runTest("Get historical prices - single day", () -> {
            LocalDateTime day = LocalDateTime.now().minusDays(50).withHour(12).withMinute(0).withSecond(0).withNano(0);
            
            // Add a price on that specific day
            priceRepo.savePricePoint(createTestPrice(
                testCompanyId1,
                day,
                TimeInterval.DAILY,
                100.0, 105.0, 99.0, 103.0, 1000000.0
            ));
            
            List<PricePoint> history = priceRepo.getHistoricalPrices(
                "PRICE1",
                day,
                day.plusDays(1),
                TimeInterval.DAILY
            );
            
            assertTrue(history.size() >= 1, "Should find at least one price for the day");
        });
    }
    
    private static void testBulkOperations() {
        System.out.println("\n=======================================================");
        System.out.println("  4. BULK OPERATIONS");
        System.out.println("=======================================================\n");
        
        // Test 4.1: Bulk save multiple price points
        runTest("Bulk save - multiple price points", () -> {
            List<PricePoint> prices = new java.util.ArrayList<>();
            LocalDateTime base = LocalDateTime.now().minusDays(20);
            
            for (int i = 0; i < 10; i++) {
                prices.add(createTestPrice(
                    testCompanyId2,
                    base.plusDays(i),
                    TimeInterval.DAILY,
                    60.0 + i, 62.0 + i, 59.0 + i, 61.0 + i, 900000.0
                ));
            }
            
            priceRepo.savePricePoints(prices);
            
            List<PricePoint> saved = priceRepo.getHistoricalPrices(
                "PRICE2",
                base.minusDays(1),
                base.plusDays(11),
                TimeInterval.DAILY
            );
            
            assertTrue(saved.size() >= 10, "Should save all 10 price points");
        });
        
        // Test 4.2: Bulk save empty list
        runTest("Bulk save - empty list", () -> {
            priceRepo.savePricePoints(List.of());
            // Should not throw exception
        });
    }
    
    private static void testEdgeCases() {
        System.out.println("\n=======================================================");
        System.out.println("  5. EDGE CASES");
        System.out.println("=======================================================\n");
        
        // Test 5.1: Very large prices
        runTest("Handle very large price values", () -> {
            PricePoint huge = createTestPrice(
                testCompanyId1,
                LocalDateTime.now().minusMinutes(5),
                TimeInterval.INTRADAY,
                999999999.99, 1000000000.0, 999999999.0, 999999999.99, 100000000000.0
            );
            
            priceRepo.savePricePoint(huge);
            // Should not throw exception
        });
        
        // Test 5.2: Very small/fractional prices
        runTest("Handle fractional penny prices", () -> {
            PricePoint tiny = createTestPrice(
                testCompanyId1,
                LocalDateTime.now().minusMinutes(10),
                TimeInterval.INTRADAY,
                0.0001, 0.0002, 0.0001, 0.00015, 100.0
            );
            
            priceRepo.savePricePoint(tiny);
            
            Optional<PricePoint> found = priceRepo.getLatestPrice("PRICE1", TimeInterval.INTRADAY);
            assertTrue(found.isPresent(), "Should save tiny prices");
        });
        
        // Test 5.3: Different time intervals
        runTest("Handle all time intervals", () -> {
            LocalDateTime now = LocalDateTime.now();
            
            for (TimeInterval interval : TimeInterval.values()) {
                PricePoint price = createTestPrice(
                    testCompanyId1, now.minusHours(1), interval,
                    100.0, 101.0, 99.0, 100.5, 1000.0
                );
                priceRepo.savePricePoint(price);
            }
            
            // Verify each interval saved
            for (TimeInterval interval : TimeInterval.values()) {
                Optional<PricePoint> found = priceRepo.getLatestPrice("PRICE1", interval);
                assertTrue(found.isPresent(), "Should save price for " + interval);
            }
        });
    }
    
    private static void testDataIntegrity() {
        System.out.println("\n=======================================================");
        System.out.println("  6. DATA INTEGRITY");
        System.out.println("=======================================================\n");
        
        // Test 6.1: Unique constraint (company_id, interval, timestamp)
        runTest("Unique constraint enforcement", () -> {
            LocalDateTime timestamp = LocalDateTime.now().minusMinutes(30);
            
            PricePoint first = createTestPrice(
                testCompanyId1, timestamp, TimeInterval.DAILY,
                110.0, 115.0, 109.0, 113.0, 1100000.0
            );
            priceRepo.savePricePoint(first);
            
            // Try to insert duplicate (should upsert/update)
            PricePoint duplicate = createTestPrice(
                testCompanyId1, timestamp, TimeInterval.DAILY,
                110.0, 116.0, 108.0, 114.0, 1200000.0
            );
            priceRepo.savePricePoint(duplicate);
            
            List<PricePoint> atTimestamp = priceRepo.getHistoricalPrices(
                "PRICE1",
                timestamp.minusSeconds(1),
                timestamp.plusSeconds(1),
                TimeInterval.DAILY
            );
            
            assertEqualsInt(1, atTimestamp.size(), "Should only have one price at exact timestamp");
            assertEqualsDouble(114.0, atTimestamp.get(0).getClose(), "Should have updated value");
        });
        
        // Test 6.2: OHLC validation (high >= low)
        runTest("OHLC data consistency", () -> {
            PricePoint valid = createTestPrice(
                testCompanyId1,
                LocalDateTime.now().minusMinutes(15),
                TimeInterval.DAILY,
                100.0, 105.0, 95.0, 102.0, 1000.0
            );
            priceRepo.savePricePoint(valid);
            
            Optional<PricePoint> found = priceRepo.getLatestPrice("PRICE1", TimeInterval.DAILY);
            assertTrue(found.isPresent(), "Valid OHLC should save");
            
            PricePoint p = found.get();
            assertTrue(p.getHigh() >= p.getLow(), "High should be >= Low");
            assertTrue(p.getHigh() >= p.getOpen(), "High should be >= Open");
            assertTrue(p.getHigh() >= p.getClose(), "High should be >= Close");
            assertTrue(p.getLow() <= p.getOpen(), "Low should be <= Open");
            assertTrue(p.getLow() <= p.getClose(), "Low should be <= Close");
        });
    }
    
    private static void testCleanup() {
        System.out.println("\n=======================================================");
        System.out.println("  7. CLEANUP OPERATIONS");
        System.out.println("=======================================================\n");
        
        // Test 7.1: Cleanup old data
        runTest("Cleanup old price data", () -> {
            // Add some very old prices
            LocalDateTime veryOld = LocalDateTime.now().minusYears(5);
            for (int i = 0; i < 3; i++) {
                priceRepo.savePricePoint(createTestPrice(
                    testCompanyId1, veryOld.plusDays(i), TimeInterval.DAILY,
                    50.0, 51.0, 49.0, 50.5, 500.0
                ));
            }
            
            // Cleanup data older than 1 year
            priceRepo.cleanup(LocalDateTime.now().minusYears(1));
            
            // Verify old data removed
            List<PricePoint> oldData = priceRepo.getHistoricalPrices(
                "PRICE1",
                veryOld.minusDays(1),
                veryOld.plusDays(10),
                TimeInterval.DAILY
            );
            
            assertEqualsInt(0, oldData.size(), "Old data should be removed");
        });
    }
    
    // ===== Test Utilities =====
    
    private static PricePoint createTestPrice(String companyId, LocalDateTime timestamp,
                                               TimeInterval interval, double open, double high,
                                               double low, double close, double volume) {
        return new PricePoint(
            companyId,  // companySymbol
            timestamp,
            interval,
            open, high, low, close, volume,
            "test-source"
        );
    }
    
    private static void setupTestData() {
        cleanupTestData();
        
        // Create test companies
        Company company1 = new Company(
            "PRICE1",
            "Price Test Company 1",
            "Test company for price data",
            1000000000.0,
            15.0
        );
        company1.setSector("Technology");
        company1.setIndustry("Software");
        companyRepo.save(company1);
        testCompanyId1 = company1.getSymbol();
        
        Company company2 = new Company(
            "PRICE2",
            "Price Test Company 2",
            "Test company for price data",
            2000000000.0,
            20.0
        );
        company2.setSector("Finance");
        company2.setIndustry("Banking");
        companyRepo.save(company2);
        testCompanyId2 = company2.getSymbol();
    }
    
    private static void cleanupTestData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete test prices
            stmt.execute("DELETE FROM public.price_points WHERE source = 'test-source' OR source = 'test'");
            // Delete test companies
            stmt.execute("DELETE FROM public.companies WHERE ticker LIKE 'PRICE%'");
        } catch (Exception e) {
            System.err.println("Warning: Cleanup failed - " + e.getMessage());
        }
    }
    
    private static void cleanupPriceData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete only test prices, keep companies
            stmt.execute("DELETE FROM public.price_points WHERE source = 'test-source' OR source = 'test'");
        } catch (Exception e) {
            System.err.println("Warning: Price cleanup failed - " + e.getMessage());
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
    
    private static void assertFalse(boolean condition, String message) {
        if (condition) {
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
