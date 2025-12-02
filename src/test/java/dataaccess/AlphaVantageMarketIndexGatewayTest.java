package dataaccess;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.Api;
import entity.MarketIndex;

class AlphaVantageMarketIndexGatewayTest {

    private AlphaVantageMarketIndexGateway gateway;
    private MockApi mockApi;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        mockApi = new MockApi();
        gateway = new AlphaVantageMarketIndexGateway(mockApi);
    }

    @Test
    void testGetMarketIndices_ReturnsThreeIndices() {
        // Act
        List<MarketIndex> indices = gateway.getMarketIndices();

        // Assert
        assertNotNull(indices);
        assertEquals(3, indices.size());
    }

    @Test
    void testGetMarketIndices_ContainsCorrectIndices() {
        // Act
        List<MarketIndex> indices = gateway.getMarketIndices();

        // Assert - Check that we got 3 different indices
        assertEquals(3, indices.size());

        // All three should have valid prices
        for (MarketIndex index : indices) {
            assertNotNull(index.getName());
            assertTrue(index.getPrice() > 0);
        }

        // Should have unique names (not all the same)
        long uniqueNames = indices.stream()
                .map(MarketIndex::getName)
                .distinct()
                .count();
        assertEquals(3, uniqueNames, "Should have 3 different index names");
    }

    @Test
    void testGetMarketIndex_SPY_ValidResponse() {
        // Act
        MarketIndex index = gateway.getMarketIndex("SPY");

        // Assert
        assertNotNull(index);
        assertTrue(index.getName().contains("S&P 500"));
        assertEquals(579.32, index.getPrice(), 0.01);
        assertEquals(0.75, index.getChange(), 0.01);
        assertEquals(0.13, index.getChangePercent(), 0.01);
        assertTrue(index.isPositive());
    }

    @Test
    void testGetMarketIndex_QQQ_ValidResponse() {
        // Act
        MarketIndex index = gateway.getMarketIndex("QQQ");

        // Assert
        assertNotNull(index);
        assertTrue(index.getName().contains("NASDAQ"));
        assertEquals(520.15, index.getPrice(), 0.01);
        assertEquals(-1.09, index.getChange(), 0.01);
        assertEquals(-0.21, index.getChangePercent(), 0.01);
        assertFalse(index.isPositive());
    }

    @Test
    void testGetMarketIndex_DIA_ValidResponse() {
        // Act
        MarketIndex index = gateway.getMarketIndex("DIA");

        // Assert
        assertNotNull(index);
        assertTrue(index.getName().contains("Dow Jones"));
        assertEquals(438.56, index.getPrice(), 0.01);
        assertEquals(0.70, index.getChange(), 0.01);
        assertEquals(0.16, index.getChangePercent(), 0.01);
        assertTrue(index.isPositive());
    }

    @Test
    void testGetMarketIndex_EmptyResponse_ReturnsDummyData() {
        // Arrange - Force empty response
        mockApi.setShouldReturnEmpty(true);

        // Act
        MarketIndex index = gateway.getMarketIndex("SPY");

        // Assert - Should return dummy data when API fails
        assertNotNull(index);
        assertTrue(index.getPrice() > 0);
    }

    // Mock Api class that returns different data for each symbol
    private static class MockApi extends Api {
        private boolean shouldReturnEmpty = false;

        public MockApi() {
            super("demo");
        }

        public void setShouldReturnEmpty(boolean empty) {
            this.shouldReturnEmpty = empty;
        }

        @Override
        public String getGlobalQuote(String symbol) {
            if (shouldReturnEmpty) {
                return "{}";
            }

            // Return different data based on symbol
            switch (symbol) {
                case "SPY":
                    return createGlobalQuoteJson("SPY", "579.32", "0.75", "0.13%");
                case "QQQ":
                    return createGlobalQuoteJson("QQQ", "520.15", "-1.09", "-0.21%");
                case "DIA":
                    return createGlobalQuoteJson("DIA", "438.56", "0.70", "0.16%");
                default:
                    return createGlobalQuoteJson(symbol, "100.0", "0.5", "0.5%");
            }
        }

        private String createGlobalQuoteJson(String symbol, String price, String change, String changePercent) {
            return String.format(
                    "{\"Global Quote\": {" +
                            "\"01. symbol\": \"%s\"," +
                            "\"05. price\": \"%s\"," +
                            "\"09. change\": \"%s\"," +
                            "\"10. change percent\": \"%s\"" +
                            "}}",
                    symbol, price, change, changePercent
            );
        }
    }
}