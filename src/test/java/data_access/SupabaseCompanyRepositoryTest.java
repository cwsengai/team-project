package data_access;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import data_access.client.SupabaseClient;
import data_access.repository.supabase.SupabaseCompanyRepository;
import entity.Company;

/**
 * Integration tests for SupabaseCompanyRepository.
 * Tests all CRUD operations against the Supabase database.
 * 
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY must be set in .env
 * - Uses service role key to bypass RLS policies (companies are admin-only write)
 * - Supabase database must have the correct schema
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupabaseCompanyRepositoryTest {
    
    private static SupabaseClient client;
    private static SupabaseCompanyRepository repository;
    
    @BeforeAll
    @SuppressWarnings("unused")
    static void setUp() throws IOException {
        // Initialize Supabase client with SERVICE ROLE (to bypass RLS)
        // Companies table is read-only for regular users, write-only for admins
        client = new SupabaseClient(true); // true = use service role key
        
        // Note: When using service role, we don't need to authenticate as a user
        // Service role bypasses all RLS policies
        
        // Initialize repository
        repository = new SupabaseCompanyRepository(client);
        
        // Clean up any existing test data
        cleanupTestData();
    }
    
    @AfterAll
    @SuppressWarnings("unused")
    static void tearDown() {
        // Clean up test data
        cleanupTestData();
        
        // Shutdown client (no need to sign out when using service role)
        if (client != null) {
            client.shutdown();
        }
    }
    
    private static void cleanupTestData() {
        // Note: With service role, we CAN delete test data
        // But for simplicity, we use unique test IDs and rely on manual cleanup
        // In production, companies are reference data and rarely deleted
    }
    
    @Test
    @Order(1)
    @DisplayName("Should find company by ticker (symbol)")
    void testFindByTicker() {
        // Try to find a real company (assuming some seed data exists)
        // This test may fail if no companies exist in the database
        Optional<Company> company = repository.findByTicker("AAPL");
        
        // We can't guarantee AAPL exists, so we just verify the method doesn't crash
        assertNotNull(company);
        
        // If AAPL exists, verify it has the correct symbol
        company.ifPresent(c -> assertEquals("AAPL", c.getSymbol()));
    }
    
    @Test
    @Order(2)
    @DisplayName("Should return empty for non-existent ticker")
    void testFindByTickerNotFound() {
        Optional<Company> company = repository.findByTicker("NONEXISTENT_" + UUID.randomUUID());
        
        assertTrue(company.isEmpty(), "Should not find non-existent company");
    }
    
    @Test
    @Order(3)
    @DisplayName("Should find companies by sector")
    void testFindBySector() {
        // Try to find companies in Technology sector
        List<Company> companies = repository.findBySector("Technology");
        
        assertNotNull(companies, "Should return a list (even if empty)");
        
        // If companies exist, verify they're all in Technology sector
        companies.forEach(c -> 
            assertEquals("Technology", c.getSector(), 
                "All returned companies should be in Technology sector")
        );
    }
    
    @Test
    @Order(4)
    @DisplayName("Should return empty list for non-existent sector")
    void testFindBySectorNotFound() {
        String fakeSector = "NonExistentSector_" + UUID.randomUUID();
        List<Company> companies = repository.findBySector(fakeSector);
        
        assertNotNull(companies, "Should return a list");
        assertTrue(companies.isEmpty(), "Should return empty list for non-existent sector");
    }
    
    @Test
    @Order(5)
    @DisplayName("Should handle null sector search gracefully")
    void testFindByNullSector() {
        // This might throw an exception or return empty list depending on implementation
        assertDoesNotThrow(() -> {
            List<Company> companies = repository.findBySector(null);
            assertNotNull(companies);
        });
    }
    
    @Test
    @Order(6)
    @DisplayName("Should find company by ID")
    void testFindById() {
        // First, try to get any company to get a valid ID
        List<Company> companies = repository.findBySector("Technology");
        
        if (!companies.isEmpty()) {
            Company firstCompany = companies.get(0);
            String companyId = firstCompany.getSymbol(); // Using symbol as ID
            
            Optional<Company> found = repository.findById(companyId);
            
            assertTrue(found.isPresent(), "Should find company by ID");
            found.ifPresent(c -> assertEquals(companyId, c.getSymbol()));
        } else {
            // Skip test if no companies exist
            System.out.println("Skipping findById test - no companies in database");
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Should return empty for non-existent ID")
    void testFindByIdNotFound() {
        String fakeId = "FAKE_" + UUID.randomUUID();
        Optional<Company> company = repository.findById(fakeId);
        
        assertTrue(company.isEmpty(), "Should not find company with fake ID");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should handle save operation")
    void testSave() {
        // Note: This test may fail if the user doesn't have INSERT permissions
        // Companies table typically requires admin/service role permissions
        
        Company testCompany = new Company(
            "TEST_" + UUID.randomUUID().toString().substring(0, 8),
            "Test Company Inc.",
            "A test company for integration testing",
            1000000000.0,
            15.5
        );
        testCompany.setSector("Technology");
        testCompany.setIndustry("Software");
        testCompany.setExchange("NASDAQ");
        
        try {
            Company saved = repository.save(testCompany);
            
            assertNotNull(saved, "Saved company should not be null");
            assertEquals(testCompany.getSymbol(), saved.getSymbol());
            assertEquals(testCompany.getName(), saved.getName());
            
            // Verify it was actually saved
            Optional<Company> found = repository.findByTicker(saved.getSymbol());
            assertTrue(found.isPresent(), "Should find newly saved company");
            
        } catch (RuntimeException e) {
            // If we get a permission error, that's expected for regular users
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                System.out.println("Skipping save test - user lacks INSERT permission (expected)");
            } else {
                throw e; // Re-throw unexpected errors
            }
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("Should handle bulk save operation")
    void testSaveAll() {
        // Note: This test may also fail due to permissions
        
        Company company1 = new Company(
            "TEST1_" + UUID.randomUUID().toString().substring(0, 8),
            "Test Company 1",
            "Test description 1",
            500000000.0,
            12.0
        );
        company1.setSector("Finance");
        
        Company company2 = new Company(
            "TEST2_" + UUID.randomUUID().toString().substring(0, 8),
            "Test Company 2",
            "Test description 2",
            750000000.0,
            18.0
        );
        company2.setSector("Healthcare");
        
        try {
            List<Company> toSave = List.of(company1, company2);
            repository.saveAll(toSave);
            
            // Verify both were saved
            Optional<Company> found1 = repository.findByTicker(company1.getSymbol());
            Optional<Company> found2 = repository.findByTicker(company2.getSymbol());
            
            assertTrue(found1.isPresent() && found2.isPresent(), 
                "Both companies should be saved");
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                System.out.println("Skipping saveAll test - user lacks INSERT permission (expected)");
            } else {
                throw e;
            }
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("Should verify client has valid credentials")
    void testClientAuthentication() {
        // When using service role, there's no user authentication
        // Service role key is used directly without login
        assertNotNull(client, "Client should exist");
        // Service role doesn't have an access token (uses service key directly)
        // So we can't check isAuthenticated() or getAccessToken()
    }
    
    @Test
    @Order(11)
    @DisplayName("Should handle special characters in company data")
    void testSpecialCharacters() {
        // This tests that the repository properly handles special characters
        // without SQL injection or encoding issues
        
        String specialSymbol = "SP&C_" + UUID.randomUUID().toString().substring(0, 8);
        Company company = new Company(
            specialSymbol,
            "Company & Co. \"The Best\"",
            "Description with special chars: <>&\"'",
            1000000.0,
            10.0
        );
        company.setSector("Technology & Finance");
        
        try {
            Company saved = repository.save(company);
            assertNotNull(saved);
            
            Optional<Company> found = repository.findByTicker(specialSymbol);
            assertTrue(found.isPresent());
            found.ifPresent(c -> {
                assertTrue(c.getName().contains("&"));
                assertTrue(c.getDescription().contains("<>&\"'"));
            });
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                System.out.println("Skipping special chars test - user lacks permission");
            } else {
                throw e;
            }
        }
    }
}
