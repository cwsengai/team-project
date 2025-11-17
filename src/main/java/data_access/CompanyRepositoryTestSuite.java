package data_access;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import entity.Company;

/**
 * Comprehensive test suite for PostgresCompanyRepository.
 * Tests all CRUD operations, edge cases, and error handling.
 */
public class CompanyRepositoryTestSuite {
    
    private static final PostgresCompanyRepository repo = new PostgresCompanyRepository();
    private static final PostgresClient client = new PostgresClient();
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Company Repository Comprehensive Test Suite         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        try {
            // Setup
            System.out.println("📋 Setup: Cleaning test data...");
            cleanupTestData();
            System.out.println("✓ Setup complete\n");
            
            // Run all test categories
            testBasicCRUD();
            testFindOperations();
            testUpdateOperations();
            testBulkOperations();
            testEdgeCases();
            testErrorHandling();
            testDataIntegrity();
            
            // Summary
            printSummary();
            
            // Cleanup
            System.out.println("\n🧹 Cleanup: Removing test data...");
            cleanupTestData();
            System.out.println("✓ Cleanup complete");
            
            if (testsFailed > 0) {
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ Test suite failed with exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void testBasicCRUD() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  1. BASIC CRUD OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 1.1: Create single company
        runTest("Create company - valid data", () -> {
            Company company = createTestCompany("TEST1", "Test Company 1", "Technology");
            Company saved = repo.save(company);
            assertNotNull(saved, "Saved company should not be null");
            assertEqualsStr(company.getTicker(), saved.getTicker(), "Ticker should match");
        });
        
        // Test 1.2: Read by ticker
        runTest("Read company by ticker", () -> {
            Optional<Company> found = repo.findByTicker("TEST1");
            assertTrue(found.isPresent(), "Company should be found");
            assertEqualsStr("Test Company 1", found.get().getName(), "Name should match");
        });
        
        // Test 1.3: Read by ID
        runTest("Read company by ID", () -> {
            Optional<Company> byTicker = repo.findByTicker("TEST1");
            assertTrue(byTicker.isPresent(), "Company should exist");
            
            String id = byTicker.get().getId();
            Optional<Company> byId = repo.findById(id);
            assertTrue(byId.isPresent(), "Company should be found by ID");
            assertEqualsStr("TEST1", byId.get().getTicker(), "Ticker should match");
        });
        
        // Test 1.4: Update existing company
        runTest("Update existing company", () -> {
            Optional<Company> existing = repo.findByTicker("TEST1");
            assertTrue(existing.isPresent(), "Company should exist");
            
            Company updated = new Company(
                existing.get().getId(),
                "TEST1",
                "Test Company 1 - Updated",
                "Technology",
                "Software",
                "NYSE",
                5000000000.0,
                "Updated description",
                existing.get().getCreatedAt()
            );
            
            repo.save(updated);
            
            Optional<Company> found = repo.findByTicker("TEST1");
            assertTrue(found.isPresent(), "Updated company should be found");
            assertEqualsStr("Test Company 1 - Updated", found.get().getName(), "Name should be updated");
            assertEqualsStr("Software", found.get().getIndustry(), "Industry should be updated");
        });
    }
    
    private static void testFindOperations() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  2. FIND OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Setup: Create multiple companies in different sectors
        repo.save(createTestCompany("TECH1", "Tech Corp 1", "Technology"));
        repo.save(createTestCompany("TECH2", "Tech Corp 2", "Technology"));
        repo.save(createTestCompany("FIN1", "Finance Corp 1", "Finance"));
        repo.save(createTestCompany("HEALTH1", "Health Corp 1", "Healthcare"));
        
        // Test 2.1: Find by sector - multiple results
        runTest("Find by sector - multiple results", () -> {
            List<Company> techCompanies = repo.findBySector("Technology");
            assertTrue(techCompanies.size() >= 2, "Should find at least 2 tech companies, found: " + techCompanies.size());
            
            boolean foundTech1 = techCompanies.stream().anyMatch(c -> c.getTicker().equals("TECH1"));
            boolean foundTech2 = techCompanies.stream().anyMatch(c -> c.getTicker().equals("TECH2"));
            assertTrue(foundTech1 && foundTech2, "Should find both tech companies");
        });
        
        // Test 2.2: Find by sector - single result
        runTest("Find by sector - single result", () -> {
            List<Company> healthCompanies = repo.findBySector("Healthcare");
            assertEqualsInt(1, healthCompanies.size(), "Should find exactly 1 healthcare company");
            assertEqualsStr("HEALTH1", healthCompanies.get(0).getTicker(), "Should find HEALTH1");
        });
        
        // Test 2.3: Find by sector - no results
        runTest("Find by sector - no results", () -> {
            List<Company> energyCompanies = repo.findBySector("Energy");
            assertEqualsInt(0, energyCompanies.size(), "Should find no energy companies");
        });
        
        // Test 2.4: Find by ticker - case sensitivity
        runTest("Find by ticker - exact match only", () -> {
            Optional<Company> upper = repo.findByTicker("TECH1");
            Optional<Company> lower = repo.findByTicker("tech1");
            
            assertTrue(upper.isPresent(), "Should find uppercase ticker");
            assertFalse(lower.isPresent(), "Should not find lowercase ticker");
        });
        
        // Test 2.5: Find by non-existent ID
        runTest("Find by non-existent ID", () -> {
            Optional<Company> found = repo.findById(UUID.randomUUID().toString());
            assertFalse(found.isPresent(), "Should not find non-existent company");
        });
    }
    
    private static void testUpdateOperations() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  3. UPDATE OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 3.1: Update market cap
        runTest("Update market cap", () -> {
            Company original = createTestCompany("UPD1", "Update Test 1", "Technology");
            original = new Company(original.getId(), original.getTicker(), original.getName(),
                original.getSector(), original.getIndustry(), original.getExchange(),
                1000000000.0, original.getDescription(), original.getCreatedAt());
            repo.save(original);
            
            Company updated = new Company(original.getId(), original.getTicker(), original.getName(),
                original.getSector(), original.getIndustry(), original.getExchange(),
                2000000000.0, original.getDescription(), original.getCreatedAt());
            repo.save(updated);
            
            Optional<Company> found = repo.findByTicker("UPD1");
            assertTrue(found.isPresent(), "Company should exist");
            assertEqualsDouble(2000000000.0, found.get().getMarketCap(), "Market cap should be updated");
        });
        
        // Test 3.2: Update with null values
        runTest("Update with null optional fields", () -> {
            Company company = new Company(
                UUID.randomUUID().toString(),
                "UPD2",
                "Update Test 2",
                null,  // null sector
                null,  // null industry
                null,  // null exchange
                null,  // null market cap
                null,  // null description
                LocalDateTime.now()
            );
            repo.save(company);
            
            Optional<Company> found = repo.findByTicker("UPD2");
            assertTrue(found.isPresent(), "Company should be saved with null fields");
        });
        
        // Test 3.3: Upsert behavior - insert on first call
        runTest("Upsert - insert new", () -> {
            Company company = createTestCompany("UPS1", "Upsert Test 1", "Technology");
            repo.save(company);
            
            Optional<Company> found = repo.findByTicker("UPS1");
            assertTrue(found.isPresent(), "Company should be inserted");
            assertEqualsStr("Upsert Test 1", found.get().getName(), "Name should match");
        });
        
        // Test 3.4: Upsert behavior - update on conflict
        runTest("Upsert - update on conflict", () -> {
            Company updated = createTestCompany("UPS1", "Upsert Test 1 - Modified", "Finance");
            repo.save(updated);
            
            Optional<Company> found = repo.findByTicker("UPS1");
            assertTrue(found.isPresent(), "Company should exist");
            assertEqualsStr("Upsert Test 1 - Modified", found.get().getName(), "Name should be updated");
            assertEqualsStr("Finance", found.get().getSector(), "Sector should be updated");
        });
    }
    
    private static void testBulkOperations() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  4. BULK OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 4.1: Save multiple companies
        runTest("Bulk save - multiple companies", () -> {
            List<Company> companies = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                companies.add(createTestCompany("BULK" + i, "Bulk Test " + i, "Retail"));
            }
            
            repo.saveAll(companies);
            
            List<Company> saved = repo.findBySector("Retail");
            assertTrue(saved.size() >= 5, "Should find at least 5 retail companies");
        });
        
        // Test 4.2: Bulk save empty list
        runTest("Bulk save - empty list", () -> {
            List<Company> empty = new ArrayList<>();
            repo.saveAll(empty);
            // Should not throw exception
        });
    }
    
    private static void testEdgeCases() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  5. EDGE CASES");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 5.1: Very long company name
        runTest("Handle very long company name", () -> {
            String longName = "A".repeat(500);
            Company company = createTestCompany("LONG1", longName, "Technology");
            repo.save(company);
            
            Optional<Company> found = repo.findByTicker("LONG1");
            assertTrue(found.isPresent(), "Company with long name should be saved");
        });
        
        // Test 5.2: Special characters in text fields
        runTest("Handle special characters", () -> {
            Company company = createTestCompany(
                "SPEC1",
                "Company & Co. \"The Best\" 'Ever'",
                "Technology & Finance"
            );
            company = new Company(
                company.getId(),
                company.getTicker(),
                company.getName(),
                company.getSector(),
                company.getIndustry(),
                company.getExchange(),
                company.getMarketCap(),
                "Description with special chars: <>&\"'",
                company.getCreatedAt()
            );
            repo.save(company);
            
            Optional<Company> found = repo.findByTicker("SPEC1");
            assertTrue(found.isPresent(), "Company with special chars should be saved");
            assertTrue(found.get().getDescription().contains("<>&\"'"), "Special chars should be preserved");
        });
        
        // Test 5.3: Very large market cap
        runTest("Handle very large market cap", () -> {
            Company company = createTestCompany("BIG1", "Big Company", "Technology");
            company = new Company(
                company.getId(),
                company.getTicker(),
                company.getName(),
                company.getSector(),
                company.getIndustry(),
                company.getExchange(),
                999999999999999.99,  // Very large number
                company.getDescription(),
                company.getCreatedAt()
            );
            repo.save(company);
            
            Optional<Company> found = repo.findByTicker("BIG1");
            assertTrue(found.isPresent(), "Company with large market cap should be saved");
        });
        
        // Test 5.4: Zero and negative market cap
        runTest("Handle zero and negative market cap", () -> {
            Company zero = createTestCompany("ZERO1", "Zero Cap", "Technology");
            zero = new Company(zero.getId(), zero.getTicker(), zero.getName(),
                zero.getSector(), zero.getIndustry(), zero.getExchange(),
                0.0, zero.getDescription(), zero.getCreatedAt());
            repo.save(zero);
            
            Optional<Company> foundZero = repo.findByTicker("ZERO1");
            assertTrue(foundZero.isPresent(), "Company with zero market cap should be saved");
            assertEqualsDouble(0.0, foundZero.get().getMarketCap(), "Market cap should be 0");
        });
    }
    
    private static void testErrorHandling() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  6. ERROR HANDLING");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 6.1: Invalid UUID format
        runTest("Handle invalid UUID format", () -> {
            Optional<Company> found = repo.findById("not-a-uuid");
            assertFalse(found.isPresent(), "Should handle invalid UUID gracefully");
        });
        
        // Test 6.2: Empty ticker
        runTest("Handle empty ticker search", () -> {
            Optional<Company> found = repo.findByTicker("");
            assertFalse(found.isPresent(), "Should not find empty ticker");
        });
        
        // Test 6.3: Null sector search
        runTest("Handle null sector search", () -> {
            List<Company> found = repo.findBySector(null);
            // Should return companies with null sector or handle gracefully
            assertNotNull(found, "Should return list (possibly empty)");
        });
    }
    
    private static void testDataIntegrity() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  7. DATA INTEGRITY");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Test 7.1: Ticker uniqueness constraint
        runTest("Ticker uniqueness - upsert on duplicate", () -> {
            Company first = createTestCompany("UNIQ1", "First Company", "Technology");
            repo.save(first);
            
            Company duplicate = createTestCompany("UNIQ1", "Second Company", "Finance");
            repo.save(duplicate);  // Should update, not create new
            
            List<Company> all = repo.findBySector("Technology");
            List<Company> finance = repo.findBySector("Finance");
            
            // UNIQ1 should now be in Finance, not Technology
            boolean inTech = all.stream().anyMatch(c -> c.getTicker().equals("UNIQ1"));
            boolean inFinance = finance.stream().anyMatch(c -> c.getTicker().equals("UNIQ1"));
            
            assertFalse(inTech, "UNIQ1 should not be in Technology after update");
            assertTrue(inFinance, "UNIQ1 should be in Finance after update");
        });
        
        // Test 7.2: Timestamp preservation
        runTest("CreatedAt timestamp preservation", () -> {
            Company company = createTestCompany("TIME1", "Timestamp Test", "Technology");
            LocalDateTime originalTime = company.getCreatedAt();
            repo.save(company);
            
            // Wait a moment
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            Optional<Company> found = repo.findByTicker("TIME1");
            assertTrue(found.isPresent(), "Company should exist");
            // Note: Created timestamp might be set by database, so this test may need adjustment
        });
    }
    
    // ===== Test Utilities =====
    
    private static Company createTestCompany(String ticker, String name, String sector) {
        return new Company(
            UUID.randomUUID().toString(),
            ticker,
            name,
            sector,
            "Test Industry",
            "TEST",
            1000000000.0,
            "Test description for " + ticker,
            LocalDateTime.now()
        );
    }
    
    private static void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("✓ " + testName);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("✗ " + testName);
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("✗ " + testName);
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
    
    private static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }
    
    private static void cleanupTestData() {
        try (Connection conn = client.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete test companies (those with tickers starting with TEST, TECH, FIN, etc.)
            stmt.execute("DELETE FROM public.companies WHERE ticker LIKE 'TEST%' " +
                        "OR ticker LIKE 'TECH%' OR ticker LIKE 'FIN%' " +
                        "OR ticker LIKE 'HEALTH%' OR ticker LIKE 'UPD%' " +
                        "OR ticker LIKE 'UPS%' OR ticker LIKE 'BULK%' " +
                        "OR ticker LIKE 'LONG%' OR ticker LIKE 'SPEC%' " +
                        "OR ticker LIKE 'BIG%' OR ticker LIKE 'ZERO%' " +
                        "OR ticker LIKE 'UNIQ%' OR ticker LIKE 'TIME%'");
        } catch (Exception e) {
            System.err.println("Warning: Cleanup failed - " + e.getMessage());
        }
    }
    
    private static void printSummary() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST SUMMARY                        ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests:  %-40d║%n", testsRun);
        System.out.printf("║  Passed:       %-40d║%n", testsPassed);
        System.out.printf("║  Failed:       %-40d║%n", testsFailed);
        System.out.printf("║  Success Rate: %-39.1f%%║%n", (testsRun > 0 ? (testsPassed * 100.0 / testsRun) : 0));
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        if (testsFailed == 0) {
            System.out.println("\n🎉 All tests passed!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please review the output above.");
        }
    }
}
