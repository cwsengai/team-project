package data_access;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

import entity.User;

/**
 * Comprehensive test suite for PostgresUserRepository.
 * Tests user CRUD operations, authentication lookups, and timestamp management.
 */
public class UserRepositoryTestSuite {
    private static UserRepository userRepo;
    private static PostgresClient client;
    
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
            System.err.println("\n❌ Test suite failed with exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void setup() throws SQLException {
        System.out.println("📋 Setup: Initializing repository and cleaning data...");
        
        userRepo = new PostgresUserRepository();
        client = new PostgresClient();
        
        // Clean up any existing test data
        cleanupTestData();
        
        System.out.println("✓ Setup complete\n");
    }

    private static void runAllTests() {
        testBasicOperations();
        cleanupTestData();
        testLookupMethods();
        cleanupTestData();
        testUpdates();
        cleanupTestData();
        testLastLoginTracking();
        cleanupTestData();
        testEdgeCases();
        cleanupTestData();
        testUniqueConstraints();
    }

    private static void testBasicOperations() {
        printSection("1. BASIC OPERATIONS");
        
        // Test 1.1: Create new user
        runTest("Create new user", () -> {
            User user = new User(null, "test1@example.com", "Test User 1");
            User saved = userRepo.save(user);
            
            assertNotNull(saved.getId(), "User ID should be generated");
            assertEqualsStr("test1@example.com", saved.getEmail(), "Email should match");
            assertEqualsStr("Test User 1", saved.getDisplayName(), "Display name should match");
            assertNotNull(saved.getCreatedAt(), "Created at should be set");
            assertNull(saved.getLastLogin(), "Last login should be null for new user");
        });
        
        // Test 1.2: Create user with minimal info
        runTest("Create user with minimal info", () -> {
            User user = new User(null, "test2@example.com", null);
            User saved = userRepo.save(user);
            
            assertNotNull(saved.getId(), "User ID should be generated");
            assertEqualsStr("test2@example.com", saved.getEmail(), "Email should match");
            assertNull(saved.getDisplayName(), "Display name can be null");
        });
        
        // Test 1.3: Update existing user
        runTest("Update existing user", () -> {
            User user = new User(null, "test3@example.com", "Original Name");
            User saved = userRepo.save(user);
            
            // Update display name
            User updated = new User(
                saved.getId(),
                saved.getEmail(),
                "Updated Name",
                saved.getCreatedAt(),
                saved.getLastLogin()
            );
            
            User result = userRepo.save(updated);
            assertEqualsStr("Updated Name", result.getDisplayName(), "Display name should be updated");
            assertEqualsStr(saved.getId(), result.getId(), "ID should remain the same");
            assertEqualsStr(saved.getEmail(), result.getEmail(), "Email should remain the same");
        });
    }

    private static void testLookupMethods() {
        printSection("2. LOOKUP METHODS");
        
        // Create test users
        User user1 = userRepo.save(new User(null, "lookup1@example.com", "Lookup User 1"));
        User user2 = userRepo.save(new User(null, "lookup2@example.com", "Lookup User 2"));
        
        // Test 2.1: Find by ID
        runTest("Find by ID", () -> {
            Optional<User> found = userRepo.findById(user1.getId());
            assertTrue(found.isPresent(), "Should find user by ID");
            assertEqualsStr("lookup1@example.com", found.get().getEmail(), "Email should match");
            assertEqualsStr("Lookup User 1", found.get().getDisplayName(), "Display name should match");
        });
        
        // Test 2.2: Find by email
        runTest("Find by email", () -> {
            Optional<User> found = userRepo.findByEmail("lookup2@example.com");
            assertTrue(found.isPresent(), "Should find user by email");
            assertEqualsStr(user2.getId(), found.get().getId(), "ID should match");
            assertEqualsStr("Lookup User 2", found.get().getDisplayName(), "Display name should match");
        });
        
        // Test 2.3: Not found by ID
        runTest("Not found by ID", () -> {
            Optional<User> found = userRepo.findById("00000000-0000-0000-0000-000000000000");
            assertTrue(!found.isPresent(), "Should not find non-existent user");
        });
        
        // Test 2.4: Not found by email
        runTest("Not found by email", () -> {
            Optional<User> found = userRepo.findByEmail("nonexistent@example.com");
            assertTrue(!found.isPresent(), "Should not find non-existent email");
        });
        
        // Test 2.5: Email lookup is case-sensitive
        runTest("Email lookup case sensitivity", () -> {
            Optional<User> found = userRepo.findByEmail("LOOKUP1@EXAMPLE.COM");
            assertTrue(!found.isPresent(), "Email lookup should be case-sensitive");
        });
    }

    private static void testUpdates() {
        printSection("3. UPDATE OPERATIONS");
        
        // Test 3.1: Update display name
        runTest("Update display name", () -> {
            User user = userRepo.save(new User(null, "update1@example.com", "Original"));
            
            User updated = new User(
                user.getId(),
                user.getEmail(),
                "Modified",
                user.getCreatedAt(),
                user.getLastLogin()
            );
            User result = userRepo.save(updated);
            
            assertEqualsStr("Modified", result.getDisplayName(), "Display name should be updated");
        });
        
        // Test 3.2: Update display name to null
        runTest("Update display name to null", () -> {
            User user = userRepo.save(new User(null, "update2@example.com", "Has Name"));
            
            User updated = new User(
                user.getId(),
                user.getEmail(),
                null,
                user.getCreatedAt(),
                user.getLastLogin()
            );
            User result = userRepo.save(updated);
            
            assertNull(result.getDisplayName(), "Display name should be null");
        });
        
        // Test 3.3: Created at timestamp preserved
        runTest("Created at timestamp preserved", () -> {
            User user = userRepo.save(new User(null, "update3@example.com", "User"));
            LocalDateTime originalCreatedAt = user.getCreatedAt();
            
            // Update the user
            User updated = new User(
                user.getId(),
                user.getEmail(),
                "Updated User",
                user.getCreatedAt(),
                user.getLastLogin()
            );
            User result = userRepo.save(updated);
            
            assertEqualsTimestamp(originalCreatedAt, result.getCreatedAt(), 
                "Created at should not change on update");
        });
    }

    private static void testLastLoginTracking() {
        printSection("4. LAST LOGIN TRACKING");
        
        // Test 4.1: Update last login
        runTest("Update last login", () -> {
            User user = userRepo.save(new User(null, "login1@example.com", "Login User"));
            LocalDateTime loginTime = LocalDateTime.now();
            
            userRepo.updateLastLogin(user.getId(), loginTime);
            
            Optional<User> updated = userRepo.findById(user.getId());
            assertTrue(updated.isPresent(), "User should exist");
            assertNotNull(updated.get().getLastLogin(), "Last login should be set");
            assertEqualsTimestamp(loginTime, updated.get().getLastLogin(), 
                "Last login timestamp should match");
        });
        
        // Test 4.2: Multiple login updates
        runTest("Multiple login updates", () -> {
            User user = userRepo.save(new User(null, "login2@example.com", "Multi Login"));
            
            LocalDateTime firstLogin = LocalDateTime.now().minusDays(2);
            userRepo.updateLastLogin(user.getId(), firstLogin);
            
            LocalDateTime secondLogin = LocalDateTime.now().minusDays(1);
            userRepo.updateLastLogin(user.getId(), secondLogin);
            
            Optional<User> updated = userRepo.findById(user.getId());
            assertTrue(updated.isPresent(), "User should exist");
            assertEqualsTimestamp(secondLogin, updated.get().getLastLogin(), 
                "Last login should be most recent");
        });
        
        // Test 4.3: Update last login preserves other fields
        runTest("Last login update preserves other fields", () -> {
            User user = userRepo.save(new User(null, "login3@example.com", "Preserve Test"));
            String originalDisplayName = user.getDisplayName();
            
            userRepo.updateLastLogin(user.getId(), LocalDateTime.now());
            
            Optional<User> updated = userRepo.findById(user.getId());
            assertTrue(updated.isPresent(), "User should exist");
            assertEqualsStr(originalDisplayName, updated.get().getDisplayName(), 
                "Display name should be preserved");
        });
    }

    private static void testEdgeCases() {
        printSection("5. EDGE CASES");
        
        // Test 5.1: Very long email
        runTest("Handle very long email", () -> {
            String longEmail = "a".repeat(50) + "@" + "b".repeat(50) + ".com";
            User user = userRepo.save(new User(null, longEmail, "Long Email User"));
            
            Optional<User> found = userRepo.findByEmail(longEmail);
            assertTrue(found.isPresent(), "Should save and find long email");
            assertEqualsStr(longEmail, found.get().getEmail(), "Email should match exactly");
        });
        
        // Test 5.2: Very long display name
        runTest("Handle very long display name", () -> {
            String longName = "A".repeat(200);
            User user = userRepo.save(new User(null, "longname@example.com", longName));
            
            Optional<User> found = userRepo.findById(user.getId());
            assertTrue(found.isPresent(), "Should save long display name");
            assertEqualsStr(longName, found.get().getDisplayName(), "Long name should be preserved");
        });
        
        // Test 5.3: Special characters in email
        runTest("Handle special characters in email", () -> {
            String specialEmail = "user+test@example-domain.co.uk";
            User user = userRepo.save(new User(null, specialEmail, "Special User"));
            
            Optional<User> found = userRepo.findByEmail(specialEmail);
            assertTrue(found.isPresent(), "Should handle special characters in email");
        });
        
        // Test 5.4: Special characters in display name
        runTest("Handle special characters in display name", () -> {
            String specialName = "Test User 李明 🎉";
            User user = userRepo.save(new User(null, "special@example.com", specialName));
            
            Optional<User> found = userRepo.findById(user.getId());
            assertTrue(found.isPresent(), "Should save special characters");
            assertEqualsStr(specialName, found.get().getDisplayName(), 
                "Special characters should be preserved");
        });
    }

    private static void testUniqueConstraints() {
        printSection("6. UNIQUE CONSTRAINTS");
        
        // Test 6.1: Duplicate email prevention
        runTest("Prevent duplicate email", () -> {
            User user1 = userRepo.save(new User(null, "unique@example.com", "User 1"));
            
            try {
                User user2 = new User(null, "unique@example.com", "User 2");
                userRepo.save(user2);
                fail("Should throw exception for duplicate email");
            } catch (RuntimeException e) {
                assertTrue(e.getMessage().contains("already exists") || 
                          e.getMessage().contains("duplicate") ||
                          e.getMessage().contains("unique"),
                    "Exception should indicate duplicate email");
            }
        });
        
        // Test 6.2: Different emails allowed
        runTest("Different emails allowed", () -> {
            User user1 = userRepo.save(new User(null, "user1@example.com", "User 1"));
            User user2 = userRepo.save(new User(null, "user2@example.com", "User 2"));
            
            assertNotNull(user1.getId(), "User 1 should be created");
            assertNotNull(user2.getId(), "User 2 should be created");
            assertTrue(!user1.getId().equals(user2.getId()), "Should have different IDs");
        });
    }

    // Helper methods
    private static void cleanupTestData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users WHERE email LIKE '%@example.com'");
        } catch (SQLException e) {
            System.err.println("Warning: Could not clean test data: " + e.getMessage());
        }
    }

    private static void cleanup() throws SQLException {
        System.out.println("\n🧹 Cleanup: Removing test data...");
        cleanupTestData();
        System.out.println("✓ Cleanup complete");
    }

    private static void runTest(String name, TestCase test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("✓ " + name);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("✗ " + name);
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("✗ " + name);
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

    private static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError(message + " (expected null, got: " + obj + ")");
        }
    }

    private static void assertEqualsStr(String expected, String actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    private static void assertEqualsTimestamp(LocalDateTime expected, LocalDateTime actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || actual == null) {
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
        // Allow 1 second difference due to database precision
        long diff = Math.abs(expected.getNano() - actual.getNano()) / 1_000_000; // milliseconds
        if (diff > 1000) {
            throw new AssertionError(message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }

    // UI helpers
    private static void printHeader() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║      User Repository Comprehensive Test Suite         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    private static void printSection(String title) {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("═══════════════════════════════════════════════════════\n");
    }

    private static void printSummary() {
        double successRate = testsRun > 0 ? (testsPassed * 100.0 / testsRun) : 0;
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST SUMMARY                        ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests:  %-36d║%n", testsRun);
        System.out.printf("║  Passed:       %-36d║%n", testsPassed);
        System.out.printf("║  Failed:       %-36d║%n", testsFailed);
        System.out.printf("║  Success Rate: %-35.1f%%║%n", successRate);
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        if (testsFailed == 0) {
            System.out.println("\n🎉 All tests passed!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please review the output above.");
            System.exit(1);
        }
    }

    @FunctionalInterface
    interface TestCase {
        void run() throws Exception;
    }
}
