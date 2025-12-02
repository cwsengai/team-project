package dataaccess;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import api.Api;
import entity.EconomicIndicator;

class AlphaVantageEconomicIndicatorGatewayTest {

    private AlphaVantageEconomicIndicatorGateway gateway;

    @BeforeEach
    @SuppressWarnings("unused")
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
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("Federal Funds")));
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("GDP")));
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("Unemployment")));
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("Treasury")));
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("CPI") || i.name().contains("Price Index")));
        assertTrue(indicators.stream().anyMatch(i -> i.name().contains("Inflation")));
    }

    @Test
    void testGetEconomicIndicators_AllHaveValidValues() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        for (EconomicIndicator indicator : indicators) {
            assertNotNull(indicator.name());
            assertNotNull(indicator.value());
            assertNotNull(indicator.lastUpdated());
            assertFalse(indicator.name().isEmpty());
            assertFalse(indicator.value().isEmpty());
        }
    }

    @Test
    void testGetEconomicIndicators_ValuesHaveCorrectFormat() {
        // Act
        List<EconomicIndicator> indicators = gateway.getEconomicIndicators();

        // Assert
        for (EconomicIndicator indicator : indicators) {
            String value = indicator.value();
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
            assertFalse(indicator.lastUpdated().isEmpty(),
                    "Last updated date should not be empty for: " + indicator.name());
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