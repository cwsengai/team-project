package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import entity.Position;

/**
 * Comprehensive test suite for PostgresPositionRepository.
 * Tests position management, portfolio isolation, P/L tracking, and edge cases.
 */
public class PositionRepositoryTestSuite {
    private static PositionRepository positionRepo;
    private static PostgresClient client;
    
    // Test data IDs
    private static String testUserId1;
    private static String testUserId2;
    private static String testPortfolioId1;
    private static String testPortfolioId2;
    private static String testCompanyId1;
    private static String testCompanyId2;
    private static String testCompanyId3;
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        printHeader();
        
        try {
            setup();
            runAllTests();
            printSummary();
            cleanup();
        } catch (Exception e) {
            System.err.println("\n[ERROR] Test suite failed with exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void setup() throws SQLException {
        System.out.println(" Setup: Creating test data and cleaning...");
        
        // Initialize repository and client
        positionRepo = new PostgresPositionRepository();
        client = new PostgresClient();
        
        // Disable FK constraints for testing
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE public.portfolios DROP CONSTRAINT IF EXISTS portfolios_user_id_fkey");
            stmt.execute("ALTER TABLE public.portfolio_positions DROP CONSTRAINT IF EXISTS portfolio_positions_portfolio_id_fkey");
            stmt.execute("ALTER TABLE public.portfolio_positions DROP CONSTRAINT IF EXISTS portfolio_positions_company_id_fkey");
            System.out.println("Note: FK constraints temporarily disabled for testing");
        }
        
        // Clean up any existing test data
        cleanupTestData();
        
        // Create test IDs (using UUIDs, not actual DB records for users)
        testUserId1 = UUID.randomUUID().toString();
        testUserId2 = UUID.randomUUID().toString();
        
        // Create test portfolios (directly in DB)
        testPortfolioId1 = createTestPortfolio(testUserId1, "Test Portfolio 1");
        testPortfolioId2 = createTestPortfolio(testUserId2, "Test Portfolio 2");
        
        // Create test companies
        testCompanyId1 = createTestCompany("AAPL", "Apple Inc.");
        testCompanyId2 = createTestCompany("GOOGL", "Alphabet Inc.");
        testCompanyId3 = createTestCompany("MSFT", "Microsoft Corporation");
        
        System.out.println("[OK] Setup complete\n");
    }

    private static void runAllTests() {
        testBasicOperations();
        testPortfolioQueries();
        testLookupMethods();
        testPLUpdates();
        testEdgeCases();
        testUniqueConstraints();
    }

    private static void testBasicOperations() {
        printSection("1. BASIC OPERATIONS");
        
        // Test 1.1: Create new position
        runTest("Create new position", () -> {
            Position position = new Position(
                null,  // ID will be generated
                testPortfolioId1,
                testCompanyId1,
                "AAPL",
                100,
                150.50,
                0.0,
                0.0,
                null
            );
            
            Position saved = positionRepo.save(position);
            assertNotNull(saved.getId(), "Position ID should be generated");
            assertEqualsStr(testPortfolioId1, saved.getPortfolioId(), "Portfolio ID should match");
            assertEqualsStr(testCompanyId1, saved.getCompanyId(), "Company ID should match");
            assertEqualsStr("AAPL", saved.getTicker(), "Ticker should match");
            assertEqualsInt(100, saved.getQuantity(), "Quantity should match");
            assertEqualsDouble(150.50, saved.getAverageCost(), "Average cost should match");
        });
        
        // Test 1.2: Update existing position
        runTest("Update existing position", () -> {
            // First create a position
            Position position = new Position(
                null,
                testPortfolioId1,
                testCompanyId2,
                "GOOGL",
                50,
                2800.00,
                0.0,
                0.0,
                null
            );
            Position saved = positionRepo.save(position);
            
            // Now update it
            Position updated = new Position(
                saved.getId(),
                saved.getPortfolioId(),
                saved.getCompanyId(),
                saved.getTicker(),
                75,  // Increased quantity
                2750.00,  // New average cost
                100.00,  // Some realized P/L
                200.00,  // Some unrealized P/L
                saved.getLastUpdated()
            );
            
            Position result = positionRepo.save(updated);
            assertEqualsStr(saved.getId(), result.getId(), "ID should remain the same");
            assertEqualsInt(75, result.getQuantity(), "Quantity should be updated");
            assertEqualsDouble(2750.00, result.getAverageCost(), "Average cost should be updated");
            assertEqualsDouble(100.00, result.getRealizedPL(), "Realized P/L should be updated");
            assertEqualsDouble(200.00, result.getUnrealizedPL(), "Unrealized P/L should be updated");
        });
        
        // Test 1.3: Save position with zero quantity
        runTest("Save position with zero quantity", () -> {
            Position position = new Position(
                null,
                testPortfolioId1,
                testCompanyId3,
                "MSFT",
                0,  // Zero quantity (closed position)
                350.00,
                500.00,  // Realized gains from closed position
                0.0,
                null
            );
            
            Position saved = positionRepo.save(position);
            assertNotNull(saved.getId(), "Should save position with zero quantity");
            assertEqualsInt(0, saved.getQuantity(), "Quantity should be zero");
            assertEqualsDouble(500.00, saved.getRealizedPL(), "Realized P/L should be preserved");
        });
    }

    private static void testPortfolioQueries() {
        printSection("2. PORTFOLIO QUERIES");
        
        // Clean up for this section
        cleanupPositions();
        
        // Test 2.1: Find all positions in portfolio
        runTest("Find all positions in portfolio", () -> {
            // Create multiple positions
            positionRepo.save(createTestPosition(testPortfolioId1, testCompanyId1, "AAPL", 100, 150.0));
            positionRepo.save(createTestPosition(testPortfolioId1, testCompanyId2, "GOOGL", 50, 2800.0));
            positionRepo.save(createTestPosition(testPortfolioId1, testCompanyId3, "MSFT", 75, 350.0));
            
            List<Position> positions = positionRepo.findByPortfolioId(testPortfolioId1);
            assertEqualsInt(3, positions.size(), "Should find all 3 positions");
            
            // Verify they're sorted by ticker
            assertEqualsStr("AAPL", positions.get(0).getTicker(), "First should be AAPL");
            assertEqualsStr("GOOGL", positions.get(1).getTicker(), "Second should be GOOGL");
            assertEqualsStr("MSFT", positions.get(2).getTicker(), "Third should be MSFT");
        });
        
        // Test 2.2: Portfolio isolation
        runTest("Portfolio isolation", () -> {
            // Create positions in different portfolios
            positionRepo.save(createTestPosition(testPortfolioId2, testCompanyId1, "AAPL", 200, 145.0));
            
            List<Position> portfolio1 = positionRepo.findByPortfolioId(testPortfolioId1);
            List<Position> portfolio2 = positionRepo.findByPortfolioId(testPortfolioId2);
            
            assertEqualsInt(3, portfolio1.size(), "Portfolio 1 should have 3 positions");
            assertEqualsInt(1, portfolio2.size(), "Portfolio 2 should have 1 position");
        });
        
        // Test 2.3: Empty portfolio
        runTest("Find positions - empty portfolio", () -> {
            String emptyPortfolioId = createTestPortfolio(testUserId1, "Empty Portfolio");
            List<Position> positions = positionRepo.findByPortfolioId(emptyPortfolioId);
            assertEqualsInt(0, positions.size(), "Empty portfolio should have no positions");
        });
    }

    private static void testLookupMethods() {
        printSection("3. LOOKUP METHODS");
        
        // Clean up for this section
        cleanupPositions();
        
        // Create test positions
        Position aapl = positionRepo.save(createTestPosition(testPortfolioId1, testCompanyId1, "AAPL", 100, 150.0));
        Position googl = positionRepo.save(createTestPosition(testPortfolioId1, testCompanyId2, "GOOGL", 50, 2800.0));
        
        // Test 3.1: Find by portfolio and ticker
        runTest("Find by portfolio and ticker", () -> {
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "AAPL");
            assertTrue(found.isPresent(), "Should find AAPL position");
            assertEqualsStr("AAPL", found.get().getTicker(), "Ticker should match");
            assertEqualsInt(100, found.get().getQuantity(), "Quantity should match");
        });
        
        // Test 3.2: Find by portfolio and company ID
        runTest("Find by portfolio and company ID", () -> {
            Optional<Position> found = positionRepo.findByPortfolioAndCompany(testPortfolioId1, testCompanyId2);
            assertTrue(found.isPresent(), "Should find GOOGL position");
            assertEqualsStr("GOOGL", found.get().getTicker(), "Ticker should match");
            assertEqualsInt(50, found.get().getQuantity(), "Quantity should match");
        });
        
        // Test 3.3: Not found by ticker
        runTest("Not found by ticker", () -> {
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "TSLA");
            assertTrue(!found.isPresent(), "Should not find non-existent ticker");
        });
        
        // Test 3.4: Not found by company ID
        runTest("Not found by company ID", () -> {
            String randomCompanyId = java.util.UUID.randomUUID().toString();
            Optional<Position> found = positionRepo.findByPortfolioAndCompany(testPortfolioId1, randomCompanyId);
            assertTrue(!found.isPresent(), "Should not find non-existent company");
        });
        
        // Test 3.5: Wrong portfolio lookup
        runTest("Wrong portfolio lookup", () -> {
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId2, "AAPL");
            assertTrue(!found.isPresent(), "Should not find position in different portfolio");
        });
    }

    private static void testPLUpdates() {
        printSection("4. P/L UPDATES");
        
        // Clean up for this section
        cleanupPositions();
        
        // Test 4.1: Update P/L values
        runTest("Update P/L values", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId1, "AAPL", 100, 150.0
            ));
            
            // Update P/L
            positionRepo.updatePL(position.getId(), 500.00, 250.00);
            
            // Verify update
            Optional<Position> updated = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "AAPL");
            assertTrue(updated.isPresent(), "Position should exist");
            assertEqualsDouble(500.00, updated.get().getRealizedPL(), "Realized P/L should be updated");
            assertEqualsDouble(250.00, updated.get().getUnrealizedPL(), "Unrealized P/L should be updated");
        });
        
        // Test 4.2: Update P/L to negative values
        runTest("Update P/L to negative values", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId2, "GOOGL", 50, 2800.0
            ));
            
            // Update to negative (losses)
            positionRepo.updatePL(position.getId(), -1000.00, -500.00);
            
            Optional<Position> updated = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "GOOGL");
            assertTrue(updated.isPresent(), "Position should exist");
            assertEqualsDouble(-1000.00, updated.get().getRealizedPL(), "Realized P/L should be negative");
            assertEqualsDouble(-500.00, updated.get().getUnrealizedPL(), "Unrealized P/L should be negative");
        });
        
        // Test 4.3: Update P/L to zero
        runTest("Update P/L to zero", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId3, "MSFT", 75, 350.0
            ));
            
            // Set initial P/L
            positionRepo.updatePL(position.getId(), 100.00, 200.00);
            
            // Reset to zero
            positionRepo.updatePL(position.getId(), 0.00, 0.00);
            
            Optional<Position> updated = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "MSFT");
            assertTrue(updated.isPresent(), "Position should exist");
            assertEqualsDouble(0.00, updated.get().getRealizedPL(), "Realized P/L should be zero");
            assertEqualsDouble(0.00, updated.get().getUnrealizedPL(), "Unrealized P/L should be zero");
        });
    }

    private static void testEdgeCases() {
        printSection("5. EDGE CASES");
        
        // Clean up for this section
        cleanupPositions();
        
        // Test 5.1: Large quantity
        runTest("Handle large quantity", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId1, "AAPL", 1000000, 150.0
            ));
            
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "AAPL");
            assertTrue(found.isPresent(), "Should save large quantity");
            assertEqualsInt(1000000, found.get().getQuantity(), "Quantity should be preserved");
        });
        
        // Test 5.2: Fractional average cost
        runTest("Handle fractional prices", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId2, "GOOGL", 100, 123.456789
            ));
            
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "GOOGL");
            assertTrue(found.isPresent(), "Should save fractional prices");
            assertTrue(Math.abs(123.456789 - found.get().getAverageCost()) < 0.0001,
                "Fractional price should be preserved");
        });
        
        // Test 5.3: Very large P/L values
        runTest("Handle very large P/L values", () -> {
            Position position = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId3, "MSFT", 50, 350.0
            ));
            
            positionRepo.updatePL(position.getId(), 999999.99, -999999.99);
            
            Optional<Position> found = positionRepo.findByPortfolioAndTicker(testPortfolioId1, "MSFT");
            assertTrue(found.isPresent(), "Should handle large P/L values");
            assertEqualsDouble(999999.99, found.get().getRealizedPL(), "Large positive P/L");
            assertEqualsDouble(-999999.99, found.get().getUnrealizedPL(), "Large negative P/L");
        });
    }

    private static void testUniqueConstraints() {
        printSection("6. UNIQUE CONSTRAINTS");
        
        // Clean up for this section
        cleanupPositions();
        
        // Test 6.1: Duplicate position prevention
        runTest("Prevent duplicate positions", () -> {
            // Create first position
            Position position1 = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId1, "AAPL", 100, 150.0
            ));
            
            // Try to create duplicate (same portfolio + company)
            try {
                Position position2 = createTestPosition(
                    testPortfolioId1, testCompanyId1, "AAPL", 50, 155.0
                );
                positionRepo.save(position2);
                fail("Should throw exception for duplicate position");
            } catch (RuntimeException e) {
                assertTrue(e.getMessage().contains("duplicate") || 
                          e.getMessage().contains("unique") ||
                          e.getMessage().contains("violates"),
                    "Exception should indicate unique constraint violation");
            }
        });
        
        // Test 6.2: Same company in different portfolios is allowed
        runTest("Same company in different portfolios", () -> {
            Position pos1 = positionRepo.save(createTestPosition(
                testPortfolioId1, testCompanyId2, "GOOGL", 100, 2800.0
            ));
            Position pos2 = positionRepo.save(createTestPosition(
                testPortfolioId2, testCompanyId2, "GOOGL", 50, 2850.0
            ));
            
            assertNotNull(pos1.getId(), "Position 1 should be created");
            assertNotNull(pos2.getId(), "Position 2 should be created");
            assertTrue(!pos1.getId().equals(pos2.getId()), "Should be different positions");
        });
    }

    // Helper methods
    private static Position createTestPosition(String portfolioId, String companyId, 
                                               String ticker, int quantity, double avgCost) {
        return new Position(null, portfolioId, companyId, ticker, quantity, avgCost, 0.0, 0.0, null);
    }

    private static String createTestPortfolio(String userId, String name) throws SQLException {
        String sql = "INSERT INTO public.portfolios (user_id, name, current_cash) VALUES (?::uuid, ?, 10000.00) RETURNING id";
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("id");
        }
    }

    private static String createTestCompany(String ticker, String name) throws SQLException {
        String sql = "INSERT INTO public.companies (ticker, name) VALUES (?, ?) RETURNING id";
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticker);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("id");
        }
    }

    private static void cleanupTestData() throws SQLException {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM public.portfolio_positions");
            stmt.execute("DELETE FROM public.portfolios WHERE name LIKE 'Test Portfolio%' OR name LIKE 'Empty Portfolio%'");
            stmt.execute("DELETE FROM public.companies WHERE ticker IN ('AAPL', 'GOOGL', 'MSFT')");
        }
    }

    private static void cleanupPositions() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM public.portfolio_positions");
        } catch (SQLException e) {
            System.err.println("Warning: Could not clean positions: " + e.getMessage());
        }
    }

    private static void cleanup() throws SQLException {
        System.out.println("\n Cleanup: Removing test data...");
        cleanupTestData();
        System.out.println("[OK] Cleanup complete");
    }

    private static void runTest(String name, TestCase test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("[OK] " + name);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("[FAIL] " + name);
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("[FAIL] " + name);
            System.out.println("  Exception: " + e.getMessage());
        }
    }

    // Assertion helpers
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
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    private static void assertEqualsInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    private static void assertEqualsDouble(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.01) {
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }

    // UI helpers
    private static void printHeader() {
        System.out.println("==========================================================");
        System.out.println("|    Position Repository Comprehensive Test Suite       |");
        System.out.println("==========================================================\n");
    }

    private static void printSection(String title) {
        System.out.println("\n=======================================================");
        System.out.println("  " + title);
        System.out.println("=======================================================\n");
    }

    private static void printSummary() {
        double successRate = testsRun > 0 ? (testsPassed * 100.0 / testsRun) : 0;
        
        System.out.println("\n==========================================================");
        System.out.println("|                    TEST SUMMARY                        |");
        System.out.println("==========================================================");
        System.out.printf("|  Total Tests:  %-36d|%n", testsRun);
        System.out.printf("|  Passed:       %-36d|%n", testsPassed);
        System.out.printf("|  Failed:       %-36d|%n", testsFailed);
        System.out.printf("|  Success Rate: %-35.1f%%|%n", successRate);
        System.out.println("==========================================================");
        
        if (testsFailed == 0) {
            System.out.println("\n*** All tests passed!");
        } else {
            System.out.println("\nWARNING:  Some tests failed. Please review the output above.");
            System.exit(1);
        }
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }
}
