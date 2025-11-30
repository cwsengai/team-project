package usecase.portfolio_statistics;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import entity.SimulatedTradeRecord;

public class PortfolioStatisticsInteractorTest {

    @Test
    void testEmptyTradesReturnsDefaults() {
        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();
        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(List.of(), 1000.0);

        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        assertEquals(0.0, out.getTotalProfit(), 1e-6);
        assertEquals(0, out.getTotalTrades());
        assertEquals(0.0, out.getWinRate(), 1e-6);
        assertNull(out.getEarliestTrade());
        assertNull(out.getLatestTrade());
    }

    @Test
    void testSimpleTradesCalculation() {
        LocalDateTime t1 = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 1, 2, 11, 0);
        LocalDateTime t3 = LocalDateTime.of(2025, 1, 3, 12, 0);

        SimulatedTradeRecord r1 = new SimulatedTradeRecord("AAPL", true, 10, 100.0, 110.0, 50.0, t1, t1.plusMinutes(5), "u");
        SimulatedTradeRecord r2 = new SimulatedTradeRecord("MSFT", true, 5, 200.0, 195.0, -20.0, t2, t2.plusMinutes(5), "u");
        SimulatedTradeRecord r3 = new SimulatedTradeRecord("TSLA", true, 2, 300.0, 315.0, 30.0, t3, t3.plusMinutes(5), "u");

        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(List.of(r1, r2, r3), 1000.0);
        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();

        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        // totalProfit = 50 + (-20) + 30 = 60
        assertEquals(60.0, out.getTotalProfit(), 1e-6);
        // maxGain should be highest positive realized PnL = 50
        assertEquals(50.0, out.getMaxGain(), 1e-6);
        // total trades = 3
        assertEquals(3, out.getTotalTrades());
        // winning trades = 2, losing =1
        assertEquals(2, out.getWinningTrades());
        assertEquals(1, out.getLosingTrades());
        // win rate = 2/3 * 100
        assertEquals((2.0/3.0)*100.0, out.getWinRate(), 1e-6);
        // earliest and latest
        assertEquals(t1, out.getEarliestTrade());
        assertEquals(t3, out.getLatestTrade());
        // total return rate = totalProfit / initialBalance * 100 = 60/1000*100 = 6%
        assertEquals(6.0, out.getTotalReturnRate(), 1e-6);
    }
}
