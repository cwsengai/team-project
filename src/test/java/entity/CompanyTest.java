package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void testFullConstructorAndGetters() {
        FinancialStatement fs = new FinancialStatement(
                "AAPL",
                "USD",
                LocalDate.now(),
                1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L,
                10L, 11L, 12L, 13L, 14L
        );

        // Create a real NewsArticle with all fields
        NewsArticle na = new NewsArticle(
                "AAPL",
                "Sample Title",
                "http://example.com",
                LocalDateTime.now(),
                "Sample summary",
                "SampleSource"
        );

        Company c = new Company(
                "AAPL",
                "Apple",
                "Tech company",
                "Technology",
                "Consumer Electronics",
                "USA",
                3000000000000L,
                5.5f,
                28.3f,
                0.8f,
                0.006f,
                1.2f,
                List.of(fs),
                List.of(na)
        );

        assertEquals("AAPL", c.getSymbol());
        assertEquals("Apple", c.getName());
        assertEquals("Tech company", c.getDescription());
        assertEquals("Technology", c.getSector());
        assertEquals("Consumer Electronics", c.getIndustry());
        assertEquals("USA", c.getCountry());
        assertEquals(3000000000000L, c.getMarketCapitalization());
        assertEquals(5.5f, c.getEps());
        assertEquals(28.3f, c.getPeRatio());
        assertEquals(0.8f, c.getDividendPerShare());
        assertEquals(0.006f, c.getDividendYield());
        assertEquals(1.2f, c.getBeta());
        assertEquals(1, c.getFinancialStatements().size());
        assertEquals(1, c.getNewsArticles().size());
    }


    @Test
    void testSimpleConstructor() {
        Company c = new Company("TSLA", "Tesla", "Electric cars", 900.0, 50.5);

        assertEquals("TSLA", c.getSymbol());
        assertEquals("Tesla", c.getName());
        assertEquals("Electric cars", c.getDescription());
        assertNull(c.getSector());
        assertNull(c.getIndustry());
        assertNull(c.getCountry());
        assertEquals(900L, c.getMarketCapitalization());
        assertEquals(50.5f, c.getPeRatio());
        assertEquals(0.0f, c.getDividendYield());
        assertEquals(1.0f, c.getBeta());
        assertNull(c.getFinancialStatements());
        assertNull(c.getNewsArticles());
    }

    @Test
    void testMinimalConstructor() {
        Company c = new Company("MSFT", "Microsoft");

        assertEquals("MSFT", c.getSymbol());
        assertEquals("Microsoft", c.getName());
        assertNull(c.getDescription());
        assertEquals(0.0f, c.getPeRatio());
        assertNull(c.getFinancialStatements());
    }

    @Test
    void testSettersModifyValues() {
        Company c = new Company("AMZN", "Amazon");

        c.setName("Amazon Corp");
        c.setDescription("E-commerce giant");
        c.setSector("Retail");
        c.setIndustry("E-commerce");
        c.setCountry("USA");
        c.setMarketCapitalization(123L);
        c.setEps(3.3f);
        c.setPeRatio(80.5f);
        c.setDividendPerShare(0.0f);
        c.setDividendYield(0.0f);
        c.setBeta(1.1f);

        assertEquals("Amazon Corp", c.getName());
        assertEquals("E-commerce giant", c.getDescription());
        assertEquals("Retail", c.getSector());
        assertEquals("E-commerce", c.getIndustry());
        assertEquals("USA", c.getCountry());
        assertEquals(123L, c.getMarketCapitalization());
        assertEquals(3.3f, c.getEps());
        assertEquals(80.5f, c.getPeRatio());
        assertEquals(0.0f, c.getDividendPerShare());
        assertEquals(0.0f, c.getDividendYield());
        assertEquals(1.1f, c.getBeta());
    }
}
