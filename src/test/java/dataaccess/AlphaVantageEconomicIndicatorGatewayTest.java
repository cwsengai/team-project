package dataaccess;

import api.Api;
import entity.EconomicIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlphaVantageEconomicIndicatorGatewayTest {

    private AlphaVantageEconomicIndicatorGateway gateway;

    @BeforeEach
    void setUp() {
        MockApi mockApi = new MockApi();
        gateway = new AlphaVantageEconomicIndicatorGateway(mockApi);
    }

    @Test
    void testGetEconomicIndicators_ReturnsSixIndicators() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        assertNotNull(indicators);
        assertEquals(6, indicators.size());
    }

    @Test
    void testGetEconomicIndicators_ContainsExpectedIndicators() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("Federal Funds")));
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("GDP")));
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("Unemployment")));
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("Treasury")));
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("CPI") || i.getName().contains("Price Index")));
        assertTrue(indicators.stream().anyMatch(i -> i.getName().contains("Inflation")));
    }

    @Test
    void testGetEconomicIndicators_AllHaveValidValues() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        for (EconomicIndicator indicator : indicators) {
            assertNotNull(indicator.getName());
            assertNotNull(indicator.getValue());
            assertNotNull(indicator.getLastUpdated());
            assertFalse(indicator.getName().isEmpty());
            assertFalse(indicator.getValue().isEmpty());
        }
    }

    @Test
    void testGetEconomicIndicators_ValuesHaveCorrectFormat() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        for (EconomicIndicator indicator : indicators) {
            String value = indicator.getValue();
            // Should contain either %, USD, or numbers
            assertTrue(
                    value.contains("%") || value.contains("USD") || value.matches(".*\\d+.*"),
                    "Value should contain %, USD, or numbers: " + value
            );
        }
    }

    @Test
    void testGetEconomicIndicators_DatesAreNotEmpty() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        for (EconomicIndicator indicator : indicators) {
            assertFalse(indicator.getLastUpdated().isEmpty(),
                    "Last updated date should not be empty for: " + indicator.getName());
        }
    }

    // Mock Api class
    private static class MockApi extends Api {
        public MockApi() {
            super("demo");
        }

        @Override
        public String getEconomicIndicator(String function, String interval) {
            // Return empty data since we use static data in the implementation
            return "{\"data\": []}";
        }
    }
}