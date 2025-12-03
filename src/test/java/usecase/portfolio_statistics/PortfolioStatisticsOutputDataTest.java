package usecase.portfolio_statistics;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PortfolioStatisticsOutputDataTest {

    @Test
    void testTradingSpanStringNoTrades() {
        PortfolioStatisticsOutputData out = new PortfolioStatisticsOutputData(
            0, 0, 0, 0, 0, 0, 0, 0, null, null
        );

        assertEquals("No trades", out.getTradingSpanString());
    }

    @Test
    void testTradingSpanStringWithTrades() {
        LocalDateTime e = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime l = LocalDateTime.of(2025, 1, 3, 17, 0);
        PortfolioStatisticsOutputData out = new PortfolioStatisticsOutputData(
            0, 0, 0, 2, 1, 1, 50.0, 0.0, e, l
        );

        assertEquals("2025-01-01 to 2025-01-03", out.getTradingSpanString());
    }
}
