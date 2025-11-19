package data_access;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import data_access.client.AuthResponse;
import data_access.client.SupabaseClient;
import data_access.repository.supabase.SupabasePortfolioRepository;
import data_access.repository.supabase.SupabasePositionRepository;
import data_access.repository.supabase.SupabaseUserRepository;
import entity.Portfolio;
import entity.Position;
import entity.User;

/**
 * Integration tests for SupabasePositionRepository.
 * Tests CRUD operations against the Supabase database.
 * 
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_ANON_KEY must be set in .env
 * - Supabase RLS policies must be configured correctly
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Supabase Position Repository Tests")
public class SupabasePositionRepositoryTest {
    
    private static SupabaseClient client;
    private static SupabasePositionRepository repository;
    private static SupabaseUserRepository userRepository;
    private static SupabasePortfolioRepository portfolioRepository;
    private static String testUserId;
    private static String testEmail;
    private static String testPortfolioId;
    private static final String TEST_PASSWORD = "TestPosition123!";
    
    @BeforeAll
    static void setUp() throws IOException {
        // Initialize Supabase client
        client = new SupabaseClient();
        
        // Create unique test user
        testEmail = "test.position." + System.currentTimeMillis() + "@test.com";
        AuthResponse authResponse = client.signUp(testEmail, TEST_PASSWORD);
        testUserId = authResponse.getUser().getId();
        System.out.println("Created test user: " + testEmail + " (ID: " + testUserId + ")");
        
        // Create user profile
        userRepository = new SupabaseUserRepository(client);
        User userProfile = new User(testUserId, testEmail, "Test Position User");
        userRepository.save(userProfile);
        
        // Create test financial instruments using SERVICE ROLE (bypasses RLS)
        SupabaseClient serviceClient = null;
        try {
            serviceClient = new SupabaseClient(true); // Use service role key
            
            Map<String, Object> aaplInstrument = new HashMap<>();
            aaplInstrument.put("symbol", "AAPL");
            aaplInstrument.put("name", "Apple Inc.");
            aaplInstrument.put("type", "stock");
            serviceClient.insert("financial_instruments", aaplInstrument, Map[].class);
            
            Map<String, Object> googlInstrument = new HashMap<>();
            googlInstrument.put("symbol", "GOOGL");
            googlInstrument.put("name", "Alphabet Inc.");
            googlInstrument.put("type", "stock");
            serviceClient.insert("financial_instruments", googlInstrument, Map[].class);
        } catch (IOException e) {
            // Instruments may already exist from previous test runs
            System.out.println("Note: Financial instruments creation skipped: " + e.getMessage());
        } finally {
            if (serviceClient != null) {
                serviceClient.shutdown();
            }
        }
        
        // Create test portfolio
        portfolioRepository = new SupabasePortfolioRepository(client);
        Portfolio portfolio = new Portfolio(null, testUserId, 100000.0);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        testPortfolioId = savedPortfolio.getId();
        
        // Initialize repository
        repository = new SupabasePositionRepository(client);
    }
    
    @AfterAll
    static void tearDown() {
        // Clean up test portfolio (positions will be cascade deleted)
        try {
            if (testPortfolioId != null) {
                portfolioRepository.delete(testPortfolioId);
            }
        } catch (Exception e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
        System.out.println("Test cleanup complete for: " + testEmail);
    }
    
    @Test
    @Order(1)
    @DisplayName("Should save a new position")
    void testSavePosition() {
        // Arrange
        Position position = new Position(
            null,  // Let database generate UUID
            testPortfolioId,
            "AAPL",
            null,  // instrumentType is transient
            10,
            150.0,
            0.0,
            0.0,
            java.time.LocalDateTime.now()
        );
        
        // Act
        Position saved = repository.save(position);
        
        // Assert
        assertNotNull(saved);
        assertEquals("AAPL", saved.getInstrumentSymbol());
        assertEquals(10, saved.getQuantity());
        assertEquals(150.0, saved.getAverageCost());
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find positions by portfolio ID")
    void testFindByPortfolioId() {
        // Arrange - add another position
        Position position2 = new Position(
            null,  // Let database generate UUID
            testPortfolioId,
            "GOOGL",
            null,  // instrumentType is transient
            5,
            2800.0,
            0.0,
            0.0,
            java.time.LocalDateTime.now()
        );
        repository.save(position2);
        
        // Act
        List<Position> positions = repository.findByPortfolioId(testPortfolioId);
        
        // Assert
        assertNotNull(positions);
        assertTrue(positions.size() >= 2);
        assertTrue(positions.stream().anyMatch(p -> p.getInstrumentSymbol().equals("AAPL")));
        assertTrue(positions.stream().anyMatch(p -> p.getInstrumentSymbol().equals("GOOGL")));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should find position by portfolio and ticker")
    void testFindByPortfolioAndTicker() {
        // Act
        Optional<Position> position = repository.findByPortfolioAndTicker(testPortfolioId, "AAPL");
        
        // Assert
        assertTrue(position.isPresent());
        assertEquals("AAPL", position.get().getInstrumentSymbol());
        assertEquals(testPortfolioId, position.get().getPortfolioId());
    }
    
    @Test
    @Order(4)
    @DisplayName("Should update existing position")
    void testUpdatePosition() {
        // Arrange - get existing position
        Optional<Position> existing = repository.findByPortfolioAndTicker(testPortfolioId, "AAPL");
        assertTrue(existing.isPresent());
        
        Position updated = new Position(
            existing.get().getId(),
            testPortfolioId,
            "AAPL",
            "stock",
            15,  // Updated quantity
            152.0,  // Updated average cost
            100.0,  // Some realized P/L
            50.0,   // Some unrealized P/L
            java.time.LocalDateTime.now()
        );
        
        // Act
        Position saved = repository.save(updated);
        
        // Assert
        assertNotNull(saved);
        assertEquals(15, saved.getQuantity());
        assertEquals(152.0, saved.getAverageCost());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should update P/L for position")
    void testUpdatePL() {
        // Arrange - get existing position
        Optional<Position> existing = repository.findByPortfolioAndTicker(testPortfolioId, "AAPL");
        assertTrue(existing.isPresent());
        String positionId = existing.get().getId();
        
        // Act
        repository.updatePL(positionId, 200.0, 75.0);
        
        // Assert
        Optional<Position> updated = repository.findByPortfolioAndTicker(testPortfolioId, "AAPL");
        assertTrue(updated.isPresent());
        assertEquals(200.0, updated.get().getRealizedPL(), 0.01);
        assertEquals(75.0, updated.get().getUnrealizedPL(), 0.01);
    }
    
    @Test
    @Order(6)
    @DisplayName("Should return empty optional for non-existent position")
    void testFindByPortfolioAndTickerNotFound() {
        // Act
        Optional<Position> position = repository.findByPortfolioAndTicker(testPortfolioId, "NONEXISTENT");
        
        // Assert
        assertFalse(position.isPresent());
    }
    
    @Test
    @Order(8)
    @DisplayName("Should return empty list for non-existent portfolio")
    void testFindByPortfolioIdEmpty() {
        // Arrange - use valid UUID that doesn't exist
        String nonExistentId = "00000000-0000-0000-0000-000000000000";
        
        // Act
        List<Position> positions = repository.findByPortfolioId(nonExistentId);
        
        // Assert
        assertNotNull(positions);
        assertTrue(positions.isEmpty());
    }
    
    @Test
    @Order(8)
    @DisplayName("Should find position by portfolio and company symbol")
    void testFindByPortfolioAndCompany() {
        // Act
        Optional<Position> position = repository.findByPortfolioAndCompany(testPortfolioId, "GOOGL");
        
        // Assert
        assertTrue(position.isPresent());
        assertEquals("GOOGL", position.get().getInstrumentSymbol());
    }
    
    @Test
    @Order(9)
    @DisplayName("Should handle position with zero quantity")
    void testPositionWithZeroQuantity() {
        // Arrange
        Position closedPosition = new Position(
            "pos-closed",
            testPortfolioId,
            "MSFT",
            "stock",
            0,  // Fully sold/closed
            350.0,
            500.0,  // Realized P/L from closing
            0.0,
            java.time.LocalDateTime.now()
        );
        
        // Act
        Position saved = repository.save(closedPosition);
        
        // Assert
        assertNotNull(saved);
        assertEquals(0, saved.getQuantity());
        assertEquals(500.0, saved.getRealizedPL(), 0.01);
    }
    
    @Test
    @Order(10)
    @DisplayName("Should maintain positions across multiple portfolios")
    void testMultiplePortfolios() throws IOException {
        // Arrange - create second portfolio
        Portfolio portfolio2 = new Portfolio(null, testUserId, 50000.0);
        Portfolio savedPortfolio2 = portfolioRepository.save(portfolio2);
        String portfolio2Id = savedPortfolio2.getId();
        
        try {
            // Add position to second portfolio
            Position position = new Position(
                null,  // Let database generate UUID
                portfolio2Id,
                "TSLA",
                null,  // instrumentType is transient
                10,
                700.0,
                0.0,
                0.0,
                java.time.LocalDateTime.now()
            );
            repository.save(position);
            
            // Act
            List<Position> portfolio1Positions = repository.findByPortfolioId(testPortfolioId);
            List<Position> portfolio2Positions = repository.findByPortfolioId(portfolio2Id);
            
            // Assert
            assertTrue(portfolio1Positions.stream().anyMatch(p -> p.getPortfolioId().equals(testPortfolioId)));
            assertTrue(portfolio2Positions.stream().anyMatch(p -> p.getPortfolioId().equals(portfolio2Id)));
            
        } finally {
            // Cleanup second portfolio
            portfolioRepository.delete(portfolio2Id);
        }
    }
}
