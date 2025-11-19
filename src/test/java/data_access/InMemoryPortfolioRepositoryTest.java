package data_access;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data_access.repository.memory.InMemoryPortfolioRepository;
import entity.Portfolio;
import entity.Position;
import entity.Trade;

/**
 * Unit tests for InMemoryPortfolioRepository.
 * Tests all CRUD operations for the in-memory implementation.
 */

@DisplayName("InMemory Portfolio Repository Tests")
public class InMemoryPortfolioRepositoryTest {
    
    private InMemoryPortfolioRepository repository;
    
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        repository = new InMemoryPortfolioRepository();
    }
    
    @Test
    @DisplayName("Should save and retrieve portfolio by ID")
    void testSaveAndFindById() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", "user-001", 10000.0);
        
        // Act
        Portfolio saved = repository.save(portfolio);
        Optional<Portfolio> retrieved = repository.findById("portfolio-001");
        
        // Assert
        assertNotNull(saved);
        assertTrue(retrieved.isPresent());
        assertEquals("portfolio-001", retrieved.get().getId());
        assertEquals("user-001", retrieved.get().getUserId());
        assertEquals(10000.0, retrieved.get().getCurrentCash());
    }
    
    @Test
    @DisplayName("Should return empty optional for non-existent portfolio")
    void testFindByIdNotFound() {
        // Act
        Optional<Portfolio> result = repository.findById("non-existent");
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    @DisplayName("Should find all portfolios by user ID")
    void testFindByUserId() {
        // Arrange
        Portfolio portfolio1 = new Portfolio("portfolio-001", "user-001", 10000.0);
        Portfolio portfolio2 = new Portfolio("portfolio-002", "user-001", 20000.0);
        Portfolio portfolio3 = new Portfolio("portfolio-003", "user-002", 15000.0);
        
        repository.save(portfolio1);
        repository.save(portfolio2);
        repository.save(portfolio3);
        
        // Act
        List<Portfolio> user1Portfolios = repository.findByUserId("user-001");
        List<Portfolio> user2Portfolios = repository.findByUserId("user-002");
        
        // Assert
        assertEquals(2, user1Portfolios.size());
        assertEquals(1, user2Portfolios.size());
        assertTrue(user1Portfolios.stream().allMatch(p -> p.getUserId().equals("user-001")));
        assertTrue(user2Portfolios.stream().allMatch(p -> p.getUserId().equals("user-002")));
    }
    
    @Test
    @DisplayName("Should return empty list when user has no portfolios")
    void testFindByUserIdEmpty() {
        // Act
        List<Portfolio> result = repository.findByUserId("non-existent-user");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should update existing portfolio")
    void testUpdatePortfolio() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", "user-001", 10000.0);
        repository.save(portfolio);
        
        // Act - modify the portfolio and save again
        Position position = new Position("AAPL");
        position.addTrade(new Trade("T001", "AAPL", 10, 150.0, LocalDateTime.now(), true));
        portfolio.addPosition(position);
        repository.save(portfolio);
        
        // Assert
        Optional<Portfolio> retrieved = repository.findById("portfolio-001");
        assertTrue(retrieved.isPresent());
        assertEquals(1, retrieved.get().getPositions().size());
        assertEquals("AAPL", retrieved.get().getPositions().get(0).getInstrumentSymbol());
    }
    
    @Test
    @DisplayName("Should update cash for portfolio")
    void testUpdateCash() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", "user-001", 10000.0);
        repository.save(portfolio);
        
        // Act
        repository.updateCash("portfolio-001", 15000.0);
        
        // Assert
        Optional<Portfolio> retrieved = repository.findById("portfolio-001");
        assertTrue(retrieved.isPresent());
        assertEquals(15000.0, retrieved.get().getCurrentCash());
    }
    
    @Test
    @DisplayName("Should handle updateCash for non-existent portfolio gracefully")
    void testUpdateCashNonExistent() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> repository.updateCash("non-existent", 5000.0));
    }
    
    @Test
    @DisplayName("Should delete portfolio")
    void testDelete() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", "user-001", 10000.0);
        repository.save(portfolio);
        
        // Act
        repository.delete("portfolio-001");
        
        // Assert
        Optional<Portfolio> retrieved = repository.findById("portfolio-001");
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    @DisplayName("Should handle delete for non-existent portfolio gracefully")
    void testDeleteNonExistent() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> repository.delete("non-existent"));
    }
    
    @Test
    @DisplayName("Should clear all portfolios")
    void testClear() {
        // Arrange
        Portfolio portfolio1 = new Portfolio("portfolio-001", "user-001", 10000.0);
        Portfolio portfolio2 = new Portfolio("portfolio-002", "user-002", 20000.0);
        repository.save(portfolio1);
        repository.save(portfolio2);
        
        // Act
        repository.clear();
        
        // Assert
        Optional<Portfolio> retrieved1 = repository.findById("portfolio-001");
        Optional<Portfolio> retrieved2 = repository.findById("portfolio-002");
        assertFalse(retrieved1.isPresent());
        assertFalse(retrieved2.isPresent());
    }
    
    @Test
    @DisplayName("Should maintain portfolio with positions and trades")
    void testPortfolioWithPositionsAndTrades() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", "user-001", 10000.0);
        
        Position aaplPosition = new Position("AAPL");
        aaplPosition.addTrade(new Trade("T001", "AAPL", 10, 150.0, LocalDateTime.now(), true));
        aaplPosition.addTrade(new Trade("T002", "AAPL", 5, 155.0, LocalDateTime.now(), true));
        
        Position googlPosition = new Position("GOOGL");
        googlPosition.addTrade(new Trade("T003", "GOOGL", 3, 2800.0, LocalDateTime.now(), true));
        
        portfolio.addPosition(aaplPosition);
        portfolio.addPosition(googlPosition);
        
        // Act
        repository.save(portfolio);
        Optional<Portfolio> retrieved = repository.findById("portfolio-001");
        
        // Assert
        assertTrue(retrieved.isPresent());
        Portfolio retrievedPortfolio = retrieved.get();
        assertEquals(2, retrievedPortfolio.getPositions().size());
        
        Position retrievedAapl = retrievedPortfolio.getPositions().stream()
                .filter(p -> p.getInstrumentSymbol().equals("AAPL"))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedAapl);
        assertEquals(2, retrievedAapl.getTrades().size());
        
        Position retrievedGoogl = retrievedPortfolio.getPositions().stream()
                .filter(p -> p.getInstrumentSymbol().equals("GOOGL"))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedGoogl);
        assertEquals(1, retrievedGoogl.getTrades().size());
    }
    
    @Test
    @DisplayName("Should handle portfolios with null userId")
    void testPortfolioWithNullUserId() {
        // Arrange
        Portfolio portfolio = new Portfolio("portfolio-001", null, 10000.0);
        repository.save(portfolio);
        
        // Act
        List<Portfolio> results = repository.findByUserId("any-user");
        
        // Assert
        assertTrue(results.isEmpty());
    }
}
