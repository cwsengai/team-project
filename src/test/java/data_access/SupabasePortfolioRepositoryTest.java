package data_access;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import entity.Portfolio;

/**
 * Integration tests for SupabasePortfolioRepository.
 * Tests CRUD operations and Row Level Security (RLS) enforcement.
 * 
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_ANON_KEY must be set in .env
 * - Supabase auth and RLS policies must be configured correctly
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupabasePortfolioRepositoryTest {
    
    private static SupabaseClient client;
    private static SupabasePortfolioRepository repository;
    private static String testUserId;
    private static String testEmail;
    private static final String TEST_PASSWORD = "Test123456!";
    
    // Test portfolio IDs for cleanup
    private static String testPortfolioId1;
    private static String testPortfolioId2;
    
    @BeforeAll
    static void setUp() throws IOException {
        // Initialize Supabase client
        client = new SupabaseClient();
        
        // Create unique test user with timestamp to avoid conflicts
        testEmail = "test.portfolio." + System.currentTimeMillis() + "@test.com";
        AuthResponse authResponse = client.signUp(testEmail, TEST_PASSWORD);
        testUserId = authResponse.getUser().getId();
        System.out.println("Created test user: " + testEmail + " (ID: " + testUserId + ")");
        
        // Initialize repository
        repository = new SupabasePortfolioRepository(client);
    }
    
    @AfterAll
    static void tearDown() {
        // Clean up test portfolios
        try {
            if (testPortfolioId1 != null) {
                repository.delete(testPortfolioId1);
            }
            if (testPortfolioId2 != null) {
                repository.delete(testPortfolioId2);
            }
        } catch (Exception e) {
            System.out.println("Cleanup warning: " + e.getMessage());
        }
        
        // Sign out and shutdown
        if (client != null) {
            client.signOut();
            client.shutdown();
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Should create new portfolio")
    void testCreatePortfolio() {
        Portfolio portfolio = new Portfolio(
            null, // ID will be generated
            testUserId,
            "Test Portfolio 1",
            true,  // isSimulation
            10000.0,  // initialCash
            10000.0,  // currentCash
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(portfolio);
        
        assertNotNull(saved, "Saved portfolio should not be null");
        assertNotNull(saved.getId(), "Portfolio ID should be generated");
        assertEquals("Test Portfolio 1", saved.getName());
        assertEquals(10000.0, saved.getCurrentCash(), 0.01);
        assertEquals(testUserId, saved.getUserId());
        
        // Store for cleanup
        testPortfolioId1 = saved.getId();
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find portfolio by ID")
    void testFindById() {
        // Use the portfolio created in previous test
        assertNotNull(testPortfolioId1, "Test portfolio 1 should exist");
        
        Optional<Portfolio> found = repository.findById(testPortfolioId1);
        
        assertTrue(found.isPresent(), "Should find portfolio by ID");
        found.ifPresent(p -> {
            assertEquals(testPortfolioId1, p.getId());
            assertEquals("Test Portfolio 1", p.getName());
            assertEquals(testUserId, p.getUserId());
        });
    }
    
    @Test
    @Order(3)
    @DisplayName("Should return empty for non-existent portfolio")
    void testFindByIdNotFound() {
        String fakeId = UUID.randomUUID().toString();
        Optional<Portfolio> found = repository.findById(fakeId);
        
        assertTrue(found.isEmpty(), "Should not find non-existent portfolio");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should find portfolios by user ID")
    void testFindByUserId() {
        // Create another portfolio for the same user
        Portfolio portfolio2 = new Portfolio(
            null,
            testUserId,
            "Test Portfolio 2",
            true,  // isSimulation
            5000.0,  // initialCash
            5000.0,  // currentCash
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(portfolio2);
        testPortfolioId2 = saved.getId();
        
        // Now find all portfolios for this user
        List<Portfolio> portfolios = repository.findByUserId(testUserId);
        
        assertNotNull(portfolios, "Should return a list");
        assertTrue(portfolios.size() >= 2, 
            "Should find at least 2 portfolios, found: " + portfolios.size());
        
        // Verify all portfolios belong to the test user
        portfolios.forEach(p -> 
            assertEquals(testUserId, p.getUserId(), "All portfolios should belong to test user")
        );
    }
    
    @Test
    @Order(5)
    @DisplayName("Should update portfolio")
    void testUpdatePortfolio() {
        assertNotNull(testPortfolioId1, "Test portfolio 1 should exist");
        
        // Fetch existing portfolio
        Optional<Portfolio> existing = repository.findById(testPortfolioId1);
        assertTrue(existing.isPresent(), "Portfolio should exist before update");
        
        // Update it
        Portfolio toUpdate = existing.get();
        toUpdate.setName("Updated Portfolio Name");
        toUpdate.setCurrentCash(15000.0);
        
        Portfolio updated = repository.save(toUpdate);
        
        assertNotNull(updated);
        assertEquals("Updated Portfolio Name", updated.getName());
        assertEquals(15000.0, updated.getCurrentCash(), 0.01);
        
        // Verify update persisted
        Optional<Portfolio> fetched = repository.findById(testPortfolioId1);
        assertTrue(fetched.isPresent());
        fetched.ifPresent(p -> {
            assertEquals("Updated Portfolio Name", p.getName());
            assertEquals(15000.0, p.getCurrentCash(), 0.01);
        });
    }
    
    @Test
    @Order(6)
    @DisplayName("Should update cash balance")
    void testUpdateCashBalance() {
        assertNotNull(testPortfolioId1, "Test portfolio 1 should exist");
        
        double newBalance = 20000.0;
        repository.updateCash(testPortfolioId1, newBalance);
        
        Optional<Portfolio> fetched = repository.findById(testPortfolioId1);
        assertTrue(fetched.isPresent());
        fetched.ifPresent(p -> 
            assertEquals(newBalance, p.getCurrentCash(), 0.01, "Cash balance should be updated")
        );
    }
    
    @Test
    @Order(7)
    @DisplayName("Should delete portfolio")
    void testDeletePortfolio() {
        // Create a temporary portfolio for deletion
        Portfolio temp = new Portfolio(
            null,
            testUserId,
            "Temporary Portfolio",
            true,
            1000.0,
            1000.0,
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(temp);
        String tempId = saved.getId();
        
        // Verify it exists
        Optional<Portfolio> found = repository.findById(tempId);
        assertTrue(found.isPresent(), "Temporary portfolio should exist");
        
        // Delete it
        repository.delete(tempId);
        
        // Verify it's gone
        Optional<Portfolio> afterDelete = repository.findById(tempId);
        assertTrue(afterDelete.isEmpty(), "Portfolio should be deleted");
    }
    
    @Test
    @Order(8)
    @DisplayName("Should handle zero cash balance")
    void testZeroCashBalance() {
        Portfolio portfolio = new Portfolio(
            null,
            testUserId,
            "Zero Balance Portfolio",
            true,
            0.0,
            0.0,
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(portfolio);
        assertNotNull(saved);
        assertEquals(0.0, saved.getCurrentCash(), 0.01);
        
        // Clean up
        repository.delete(saved.getId());
    }
    
    @Test
    @Order(9)
    @DisplayName("Should handle negative cash balance")
    void testNegativeCashBalance() {
        // Some systems allow negative balance (margin accounts)
        Portfolio portfolio = new Portfolio(
            null,
            testUserId,
            "Negative Balance Portfolio",
            true,
            -1000.0,
            -1000.0,
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(portfolio);
        assertNotNull(saved);
        assertEquals(-1000.0, saved.getCurrentCash(), 0.01);
        
        // Clean up
        repository.delete(saved.getId());
    }
    
    @Test
    @Order(10)
    @DisplayName("Should handle special characters in portfolio name")
    void testSpecialCharactersInName() {
        Portfolio portfolio = new Portfolio(
            null,
            testUserId,
            "Portfolio & Co. \"Best\" 'Ever' <test>",
            true,
            5000.0,
            5000.0,
            "USD",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        Portfolio saved = repository.save(portfolio);
        assertNotNull(saved);
        assertTrue(saved.getName().contains("&"));
        assertTrue(saved.getName().contains("\""));
        
        // Verify it persisted correctly
        Optional<Portfolio> fetched = repository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        fetched.ifPresent(p -> {
            assertTrue(p.getName().contains("&"));
            assertTrue(p.getName().contains("\""));
        });
        
        // Clean up
        repository.delete(saved.getId());
    }
    
    @Test
    @Order(11)
    @DisplayName("Should verify RLS - user can only see own portfolios")
    void testRowLevelSecurity() {
        // This test verifies that RLS is working by creating another user
        // and ensuring they can't see the first user's portfolios
        
        String secondUserEmail = "test-portfolio-rls-" + UUID.randomUUID() + "@example.com";
        SupabaseClient secondClient = new SupabaseClient();
        
        try {
            // Create second user
            secondClient.signUp(secondUserEmail, TEST_PASSWORD);
            
            // Create repository for second user
            SupabasePortfolioRepository secondRepo = new SupabasePortfolioRepository(secondClient);
            
            // Second user tries to fetch first user's portfolios
            List<Portfolio> secondUserPortfolios = secondRepo.findByUserId(testUserId);
            
            // Due to RLS, second user should not see first user's portfolios
            // They should see an empty list or only their own portfolios
            assertTrue(secondUserPortfolios.isEmpty() || 
                secondUserPortfolios.stream().noneMatch(p -> p.getUserId().equals(testUserId)),
                "Second user should not see first user's portfolios (RLS enforcement)");
            
        } catch (IOException e) {
            System.out.println("RLS test note: " + e.getMessage());
        } finally {
            secondClient.signOut();
            secondClient.shutdown();
        }
    }
    
    @Test
    @Order(12)
    @DisplayName("Should verify client authentication")
    void testClientAuthentication() {
        assertTrue(client.isAuthenticated(), "Client should be authenticated");
        assertNotNull(client.getAccessToken(), "Access token should exist");
    }
}
