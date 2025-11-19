package data_access;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import data_access.repository.supabase.SupabaseTradeRepository;
import data_access.repository.supabase.SupabaseUserRepository;
import entity.Portfolio;
import entity.Trade;
import entity.TradeType;
import entity.User;

/**
 * Integration tests for SupabaseTradeRepository.
 * Tests all operations against the Supabase database.
 * <p>
 * NOTE: These tests require financial instruments (AAPL, GOOGL) to exist in the database.
 * Due to Row Level Security (RLS) policies, regular users cannot create financial instruments.
 * These instruments must be created by an admin or service role before running tests.
 * <p>
 * Prerequisites:
 * - SUPABASE_URL and SUPABASE_ANON_KEY must be set in .env
 * - Supabase RLS policies must be configured correctly
 * - Financial instruments (AAPL, GOOGL) must exist in database
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Supabase Trade Repository Tests")
public class SupabaseTradeRepositoryTest {

    private static SupabaseTradeRepository repository;
    private static SupabasePortfolioRepository portfolioRepository;
    private static String testEmail;
    private static String testPortfolioId;
    private static final String TEST_PASSWORD = "TestTrade123!";
    @SuppressWarnings({"unused"})
    private static final boolean INSTRUMENTS_EXIST = false;
    private static final boolean CLEANUP_AFTER_TESTS = true;
    
    @BeforeAll
    @SuppressWarnings("unused")
    static void setUp() throws IOException {
        // Initialize Supabase client
        SupabaseClient client = new SupabaseClient();
        
        // Create unique test user
        testEmail = "test.trade." + System.currentTimeMillis() + "@test.com";
        AuthResponse authResponse = client.signUp(testEmail, TEST_PASSWORD);
        String testUserId = authResponse.getUser().getId();
        System.out.println("Created test user: " + testEmail + " (ID: " + testUserId + ")");
        
        // Create user profile
        SupabaseUserRepository userRepository = new SupabaseUserRepository(client);
        User userProfile = new User(testUserId, testEmail, "Test Trade User");
        userRepository.save(userProfile);
        
        // Create test financial instruments using SERVICE ROLE (bypasses RLS)
        // Each instrument in separate try-catch to handle duplicates gracefully
        try (SupabaseClient serviceClient = new SupabaseClient(true)) {
            try {
                Map<String, Object> aaplInstrument = new HashMap<>();
                aaplInstrument.put("symbol", "AAPL");
                aaplInstrument.put("name", "Apple Inc.");
                aaplInstrument.put("type", "stock");
                serviceClient.insert("financial_instruments", aaplInstrument, Map[].class);
            } catch (IOException e) { /* Already exists */ }
            
            try {
                Map<String, Object> googlInstrument = new HashMap<>();
                googlInstrument.put("symbol", "GOOGL");
                googlInstrument.put("name", "Alphabet Inc.");
                googlInstrument.put("type", "stock");
                serviceClient.insert("financial_instruments", googlInstrument, Map[].class);
            } catch (IOException e) { /* Already exists */ }
        }
        
        // Create test portfolio
        portfolioRepository = new SupabasePortfolioRepository(client);
        Portfolio portfolio = new Portfolio(null, testUserId, 50000.0);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        testPortfolioId = savedPortfolio.getId();
        
        // Initialize repository
        repository = new SupabaseTradeRepository(client);
    }
    
    @AfterAll
    @SuppressWarnings("unused")
    static void tearDown() {
        // Clean up test trades if cleanup is enabled
        if (CLEANUP_AFTER_TESTS && testPortfolioId != null) {
            try {
                List<Trade> trades = repository.findByPortfolioId(testPortfolioId);
                for (Trade trade : trades) {
                    repository.delete(trade.getId());
                }
                System.out.println("Cleaned up " + trades.size() + " test trades");
            } catch (Exception e) {
                System.out.println("Trade cleanup warning: " + e.getMessage());
            }
        }
        
        // Clean up test portfolio
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
    @DisplayName("Should save a new trade")
    void testSaveTrade() {
        // Arrange
        Trade trade = new Trade(
            null,  // Let database generate ID
            testPortfolioId,
            null,
            "AAPL",
            null,  // instrumentType is transient, not stored in DB
            TradeType.BUY,
            10,
            150.0,
            10.0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Act
        Trade saved = repository.save(trade);
        
        // Assert
        assertNotNull(saved);
        assertEquals("AAPL", saved.getInstrumentSymbol());
        assertEquals(TradeType.BUY, saved.getTradeType());
        assertEquals(10, saved.getQuantity());
        assertEquals(150.0, saved.getPrice());
    }
    
    @Test
    @Order(2)
    @DisplayName("Should find trades by portfolio ID")
    void testFindByPortfolioId() {
        // Arrange - add another trade
        Trade trade2 = new Trade(
            null,  // Let database generate ID
            testPortfolioId,
            null,
            "GOOGL",
            null,  // instrumentType is transient
            TradeType.BUY,
            5,
            2800.0,
            10.0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        repository.save(trade2);
        
        // Act
        List<Trade> trades = repository.findByPortfolioId(testPortfolioId);
        
        // Assert
        assertNotNull(trades);
        assertTrue(trades.size() >= 2);
        assertTrue(trades.stream().anyMatch(t -> t.getInstrumentSymbol().equals("AAPL")));
        assertTrue(trades.stream().anyMatch(t -> t.getInstrumentSymbol().equals("GOOGL")));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should find trades by portfolio in date range")
    void testFindByPortfolioInDateRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        
        // Act
        List<Trade> trades = repository.findByPortfolioInDateRange(testPortfolioId, start, end);
        
        // Assert
        assertNotNull(trades);
        assertTrue(trades.size() >= 2);
    }
    
    @Test
    @Order(4)
    @DisplayName("Should return empty list for non-existent portfolio")
    void testFindByPortfolioIdEmpty() {
        // Arrange - use a valid UUID format that doesn't exist
        String nonExistentPortfolioId = "00000000-0000-0000-0000-000000000000";
        
        // Act
        List<Trade> trades = repository.findByPortfolioId(nonExistentPortfolioId);
        
        // Assert
        assertNotNull(trades);
        assertTrue(trades.isEmpty());
    }
    
    @Test
    @Order(5)
    @DisplayName("Should handle sell trades")
    void testSellTrade() {
        // Arrange
        Trade sellTrade = new Trade(
            null,  // Let database generate ID
            testPortfolioId,
            null,
            "AAPL",
            null,  // instrumentType is transient
            TradeType.SELL,
            5,
            155.0,
            5.0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Act
        Trade saved = repository.save(sellTrade);
        
        // Assert
        assertNotNull(saved);
        assertEquals(TradeType.SELL, saved.getTradeType());
        assertEquals(5, saved.getQuantity());
    }
    
    @Test
    @Order(6)
    @DisplayName("Should find trades ordered by execution time descending")
    void testTradesOrderedByExecutionTime() {
        // Act
        List<Trade> trades = repository.findByPortfolioId(testPortfolioId);
        
        // Assert
        assertTrue(trades.size() >= 2);
        // Verify descending order (most recent first)
        for (int i = 0; i < trades.size() - 1; i++) {
            assertTrue(
                trades.get(i).getExecutedAt().isAfter(trades.get(i + 1).getExecutedAt()) ||
                trades.get(i).getExecutedAt().isEqual(trades.get(i + 1).getExecutedAt())
            );
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Should return empty list for date range with no trades")
    void testFindByDateRangeEmpty() {
        // Arrange - date range in the past
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().minusMonths(11);
        
        // Act
        List<Trade> trades = repository.findByPortfolioInDateRange(testPortfolioId, start, end);
        
        // Assert
        assertNotNull(trades);
        assertTrue(trades.isEmpty());
    }
    
    @Test
    @Order(8)
    @DisplayName("Should delete trade")
    void testDeleteTrade() {
        // Arrange - create a temporary trade for deletion
        Trade tempTrade = new Trade(
            null,
            testPortfolioId,
            null,
            "AAPL",
            null,
            TradeType.BUY,
            1,
            150.0,
            1.0,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Trade saved = repository.save(tempTrade);
        String tradeId = saved.getId();
        
        // Verify it exists
        List<Trade> beforeDelete = repository.findByPortfolioId(testPortfolioId);
        assertTrue(beforeDelete.stream().anyMatch(t -> t.getId().equals(tradeId)), "Trade should exist");
        
        // Act - delete it
        repository.delete(tradeId);
        
        // Assert - verify it's gone
        List<Trade> afterDelete = repository.findByPortfolioId(testPortfolioId);
        assertFalse(afterDelete.stream().anyMatch(t -> t.getId().equals(tradeId)), "Trade should be deleted");
    }
}
