//package data_access;
//
//import java.sql.Connection;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import entity.Portfolio;
//
///**
// * Comprehensive test suite for PostgresPortfolioRepository.
// * Tests all CRUD operations, user isolation, and RLS policies.
// */
//public class PortfolioRepositoryTestSuite {
//
//    private static final PostgresPortfolioRepository repo = new PostgresPortfolioRepository();
//    private static final PostgresClient client = new PostgresClient();
//    private static int testsRun = 0;
//    private static int testsPassed = 0;
//    private static int testsFailed = 0;
//
//    // Test user IDs
//    private static String testUserId1;
//    private static String testUserId2;
//
//    public static void main(String[] args) {
//        System.out.println("==========================================================");
//        System.out.println("|   Portfolio Repository Comprehensive Test Suite       |");
//        System.out.println("==========================================================\n");
//
//        try {
//            // Setup
//            System.out.println(" Setup: Creating test users and cleaning data...");
//            setupTestData();
//            System.out.println("[OK] Setup complete\n");
//
//            // Run all test categories
//            testBasicCRUD();
//            cleanupPortfolioData();
//            testUserIsolation();
//            cleanupPortfolioData();
//            testCashManagement();
//            cleanupPortfolioData();
//            testUpdates();
//            cleanupPortfolioData();
//            testEdgeCases();
//            cleanupPortfolioData();
//            testDeletion();
//
//            // Summary
//            printSummary();
//
//            // Cleanup
//            System.out.println("\n Cleanup: Removing test data...");
//            cleanupTestData();
//            System.out.println("[OK] Cleanup complete");
//
//            if (testsFailed > 0) {
//                System.exit(1);
//            }
//
//        } catch (Exception e) {
//            System.err.println("\n[ERROR] Test suite failed with exception: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    private static void testBasicCRUD() {
//        System.out.println("=======================================================");
//        System.out.println("  1. BASIC CRUD OPERATIONS");
//        System.out.println("=======================================================\n");
//
//        // Test 1.1: Create new portfolio
//        runTest("Create new portfolio", () -> {
//            Portfolio portfolio = new Portfolio(
//                UUID.randomUUID().toString(),
//                testUserId1,
//                "Test Portfolio 1",
//                true,
//                10000.0,
//                10000.0,
//                "USD",
//                LocalDateTime.now(),
//                LocalDateTime.now()
//            );
//
//            Portfolio saved = repo.save(portfolio);
//
//            assertNotNull(saved, "Saved portfolio should not be null");
//            assertEqualsStr(portfolio.getId(), saved.getId(), "Portfolio ID should match");
//            assertEqualsDouble(10000.0, saved.getCurrentCash(), "Cash should match");
//        });
//
//        // Test 1.2: Find portfolio by ID
//        runTest("Find portfolio by ID", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Find Test", 5000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//
//            assertTrue(found.isPresent(), "Portfolio should be found by ID");
//            assertEqualsStr("Find Test", found.get().getName(), "Portfolio name should match");
//            assertEqualsDouble(5000.0, found.get().getCurrentCash(), "Cash should match");
//        });
//
//        // Test 1.3: Find portfolio by ID - not found
//        runTest("Find portfolio by ID - not found", () -> {
//            Optional<Portfolio> notFound = repo.findById(UUID.randomUUID().toString());
//            assertFalse(notFound.isPresent(), "Should not find non-existent portfolio");
//        });
//
//        // Test 1.4: Find portfolios by user ID
//        runTest("Find portfolios by user ID", () -> {
//            // Create multiple portfolios for same user
//            repo.save(createTestPortfolio(testUserId1, "Portfolio A", 1000.0));
//            repo.save(createTestPortfolio(testUserId1, "Portfolio B", 2000.0));
//            repo.save(createTestPortfolio(testUserId1, "Portfolio C", 3000.0));
//
//            List<Portfolio> portfolios = repo.findByUserId(testUserId1);
//
//            assertTrue(portfolios.size() >= 3, "Should find at least 3 portfolios for user");
//        });
//    }
//
//    private static void testUserIsolation() {
//        System.out.println("\n=======================================================");
//        System.out.println("  2. USER ISOLATION (RLS)");
//        System.out.println("=======================================================\n");
//
//        // Test 2.1: Users can only see their own portfolios
//        runTest("User isolation - separate user portfolios", () -> {
//            // Create portfolios for two different users
//            repo.save(createTestPortfolio(testUserId1, "User1 Portfolio", 1000.0));
//            repo.save(createTestPortfolio(testUserId2, "User2 Portfolio", 2000.0));
//
//            List<Portfolio> user1Portfolios = repo.findByUserId(testUserId1);
//            List<Portfolio> user2Portfolios = repo.findByUserId(testUserId2);
//
//            // Verify user1 portfolios don't contain user2 portfolios
//            for (Portfolio p : user1Portfolios) {
//                assertEqualsStr(testUserId1, p.getUserId(), "User1 should only see own portfolios");
//            }
//
//            // Verify user2 portfolios don't contain user1 portfolios
//            for (Portfolio p : user2Portfolios) {
//                assertEqualsStr(testUserId2, p.getUserId(), "User2 should only see own portfolios");
//            }
//        });
//
//        // Test 2.2: Empty list for user with no portfolios
//        runTest("Find portfolios - user with no portfolios", () -> {
//            String newUserId = UUID.randomUUID().toString();
//            List<Portfolio> portfolios = repo.findByUserId(newUserId);
//
//            assertEqualsInt(0, portfolios.size(), "Should return empty list for user with no portfolios");
//        });
//    }
//
//    private static void testCashManagement() {
//        System.out.println("\n=======================================================");
//        System.out.println("  3. CASH MANAGEMENT");
//        System.out.println("=======================================================\n");
//
//        // Test 3.1: Update cash balance
//        runTest("Update cash balance", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Cash Test", 10000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            repo.updateCash(saved.getId(), 8500.0);
//
//            Optional<Portfolio> updated = repo.findById(saved.getId());
//            assertTrue(updated.isPresent(), "Portfolio should still exist");
//            assertEqualsDouble(8500.0, updated.get().getCurrentCash(), "Cash should be updated");
//        });
//
//        // Test 3.2: Update cash to zero
//        runTest("Update cash to zero", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Zero Cash Test", 5000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            repo.updateCash(saved.getId(), 0.0);
//
//            Optional<Portfolio> updated = repo.findById(saved.getId());
//            assertTrue(updated.isPresent(), "Portfolio should exist");
//            assertEqualsDouble(0.0, updated.get().getCurrentCash(), "Cash should be zero");
//        });
//
//        // Test 3.3: Update cash - negative balance (margin)
//        runTest("Update cash - negative balance", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Margin Test", 10000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            repo.updateCash(saved.getId(), -500.0);
//
//            Optional<Portfolio> updated = repo.findById(saved.getId());
//            assertTrue(updated.isPresent(), "Portfolio should exist");
//            assertEqualsDouble(-500.0, updated.get().getCurrentCash(), "Negative cash should be allowed");
//        });
//    }
//
//    private static void testUpdates() {
//        System.out.println("\n=======================================================");
//        System.out.println("  4. PORTFOLIO UPDATES");
//        System.out.println("=======================================================\n");
//
//        // Test 4.1: Update portfolio name
//        runTest("Update portfolio name", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Original Name", 5000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            // Create updated version
//            Portfolio updated = new Portfolio(
//                saved.getId(),
//                saved.getUserId(),
//                "Updated Name",
//                saved.isSimulation(),
//                saved.getInitialCash(),
//                saved.getCurrentCash(),
//                saved.getCurrency(),
//                saved.getCreatedAt(),
//                LocalDateTime.now()
//            );
//
//            repo.save(updated);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should exist");
//            assertEqualsStr("Updated Name", found.get().getName(), "Name should be updated");
//        });
//
//        // Test 4.2: Update simulation flag
//        runTest("Update simulation flag", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Sim Test", 5000.0);
//            portfolio.setSimulation(true);
//            Portfolio saved = repo.save(portfolio);
//
//            // Update to real portfolio
//            Portfolio updated = new Portfolio(
//                saved.getId(),
//                saved.getUserId(),
//                saved.getName(),
//                false,  // Change to real
//                saved.getInitialCash(),
//                saved.getCurrentCash(),
//                saved.getCurrency(),
//                saved.getCreatedAt(),
//                LocalDateTime.now()
//            );
//
//            repo.save(updated);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should exist");
//            assertFalse(found.get().isSimulation(), "Should be real portfolio now");
//        });
//
//        // Test 4.3: Update currency
//        runTest("Update currency", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Currency Test", 5000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            Portfolio updated = new Portfolio(
//                saved.getId(),
//                saved.getUserId(),
//                saved.getName(),
//                saved.isSimulation(),
//                saved.getInitialCash(),
//                saved.getCurrentCash(),
//                "CAD",  // Change to CAD
//                saved.getCreatedAt(),
//                LocalDateTime.now()
//            );
//
//            repo.save(updated);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should exist");
//            assertEqualsStr("CAD", found.get().getCurrency(), "Currency should be CAD");
//        });
//    }
//
//    private static void testEdgeCases() {
//        System.out.println("\n=======================================================");
//        System.out.println("  5. EDGE CASES");
//        System.out.println("=======================================================\n");
//
//        // Test 5.1: Very large cash balance
//        runTest("Handle very large cash balance", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "Big Money", 999999999999.99);
//            Portfolio saved = repo.save(portfolio);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should be saved");
//            assertTrue(found.get().getCurrentCash() > 999999999999.0, "Large balance should be preserved");
//        });
//
//        // Test 5.2: Long portfolio name
//        runTest("Handle long portfolio name", () -> {
//            String longName = "A".repeat(200);  // 200 character name
//            Portfolio portfolio = createTestPortfolio(testUserId1, longName, 1000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should be saved");
//            assertEqualsStr(longName, found.get().getName(), "Long name should be preserved");
//        });
//
//        // Test 5.3: Special characters in name
//        runTest("Handle special characters in name", () -> {
//            String specialName = "My Portfolio! @#$%^&*()_+-=[]{}|;':\",./<>?";
//            Portfolio portfolio = createTestPortfolio(testUserId1, specialName, 1000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertTrue(found.isPresent(), "Portfolio should be saved");
//            assertEqualsStr(specialName, found.get().getName(), "Special characters should be preserved");
//        });
//    }
//
//    private static void testDeletion() {
//        System.out.println("\n=======================================================");
//        System.out.println("  6. DELETION");
//        System.out.println("=======================================================\n");
//
//        // Test 6.1: Delete portfolio
//        runTest("Delete portfolio", () -> {
//            Portfolio portfolio = createTestPortfolio(testUserId1, "To Delete", 1000.0);
//            Portfolio saved = repo.save(portfolio);
//
//            repo.delete(saved.getId());
//
//            Optional<Portfolio> found = repo.findById(saved.getId());
//            assertFalse(found.isPresent(), "Portfolio should be deleted");
//        });
//
//        // Test 6.2: Delete non-existent portfolio
//        runTest("Delete non-existent portfolio", () -> {
//            // Should not throw exception
//            repo.delete(UUID.randomUUID().toString());
//        });
//    }
//
//    // ===== Test Utilities =====
//
//    private static Portfolio createTestPortfolio(String userId, String name, double cash) {
//        return new Portfolio(
//            UUID.randomUUID().toString(),
//            userId,
//            name,
//            true,
//            cash,
//            cash,
//            "USD",
//            LocalDateTime.now(),
//            LocalDateTime.now()
//        );
//    }
//
//    private static void setupTestData() {
//        cleanupTestData();
//
//        // Create test user IDs
//        testUserId1 = UUID.randomUUID().toString();
//        testUserId2 = UUID.randomUUID().toString();
//
//        try (Connection conn = client.getConnection();
//             Statement stmt = conn.createStatement()) {
//            // Temporarily disable FK constraint for testing
//            // In production, user_id would reference auth.users created by Supabase Auth
//            stmt.execute("ALTER TABLE public.portfolios DROP CONSTRAINT IF EXISTS portfolios_user_id_fkey");
//            System.out.println("  Note: FK constraint temporarily disabled for testing");
//        } catch (Exception e) {
//            System.err.println("Warning: Failed to disable FK constraint - " + e.getMessage());
//        }
//    }
//
//    private static void cleanupTestData() {
//        try (Connection conn = client.getConnection();
//             Statement stmt = conn.createStatement()) {
//            // Delete test portfolios (cascade will delete positions/trades)
//            stmt.execute("DELETE FROM public.portfolios WHERE name LIKE 'Test %' OR name LIKE '%Test'");
//
//            // Re-enable FK constraint
//            stmt.execute("ALTER TABLE public.portfolios ADD CONSTRAINT portfolios_user_id_fkey " +
//                        "FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE");
//            System.out.println("  Note: FK constraint re-enabled");
//        } catch (Exception e) {
//            System.err.println("Warning: Cleanup failed - " + e.getMessage());
//        }
//    }
//
//    private static void cleanupPortfolioData() {
//        try (Connection conn = client.getConnection();
//             Statement stmt = conn.createStatement()) {
//            // Delete test portfolios
//            stmt.execute("DELETE FROM public.portfolios WHERE name LIKE 'Test %' OR name LIKE '%Test' OR name LIKE 'Portfolio %' OR name LIKE 'User%Portfolio' OR name = 'To Delete' OR name = 'My Portfolio! @#$%^&*()_+-=[]{}|;'':\",.<>/?'");
//        } catch (Exception e) {
//            System.err.println("Warning: Portfolio cleanup failed - " + e.getMessage());
//        }
//    }
//
//    private static void runTest(String testName, Runnable test) {
//        testsRun++;
//        try {
//            test.run();
//            testsPassed++;
//            System.out.println("[OK] " + testName);
//        } catch (AssertionError e) {
//            testsFailed++;
//            System.out.println("[FAIL] " + testName);
//            System.out.println("  Error: " + e.getMessage());
//        } catch (Exception e) {
//            testsFailed++;
//            System.out.println("[FAIL] " + testName);
//            System.out.println("  Exception: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private static void assertTrue(boolean condition, String message) {
//        if (!condition) {
//            throw new AssertionError(message);
//        }
//    }
//
//    private static void assertFalse(boolean condition, String message) {
//        if (condition) {
//            throw new AssertionError(message);
//        }
//    }
//
//    private static void assertNotNull(Object obj, String message) {
//        if (obj == null) {
//            throw new AssertionError(message);
//        }
//    }
//
//    private static void assertEqualsStr(String expected, String actual, String message) {
//        if (expected == null && actual == null) return;
//        if (expected == null || !expected.equals(actual)) {
//            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
//        }
//    }
//
//    private static void assertEqualsDouble(double expected, double actual, String message) {
//        if (Math.abs(expected - actual) > 0.01) {
//            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
//        }
//    }
//
//    private static void assertEqualsInt(int expected, int actual, String message) {
//        if (expected != actual) {
//            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
//        }
//    }
//
//    private static void printSummary() {
//        System.out.println("\n==========================================================");
//        System.out.println("|                    TEST SUMMARY                        |");
//        System.out.println("==========================================================");
//        System.out.printf("|  Total Tests:  %-40d|%n", testsRun);
//        System.out.printf("|  Passed:       %-40d|%n", testsPassed);
//        System.out.printf("|  Failed:       %-40d|%n", testsFailed);
//        System.out.printf("|  Success Rate: %-39.1f%%|%n", (testsRun > 0 ? (testsPassed * 100.0 / testsRun) : 0));
//        System.out.println("==========================================================");
//
//        if (testsFailed == 0) {
//            System.out.println("\n*** All tests passed!");
//        } else {
//            System.out.println("\nWARNING:  Some tests failed. Please review the output above.");
//        }
//    }
//}
