package usecase.portfolio_statistics;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import entity.SimulatedTradeRecord;

public class PortfolioStatisticsInteractorTest {

    @Test
    void calculateStatistics_noTrades_returnsZeroedOutput() {
        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();

        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(List.of(), 1000.0);
        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        assertEquals(0.0, out.getTotalProfit(), 1e-6);
        assertEquals(0.0, out.getMaxGain(), 1e-6);
        assertEquals(0.0, out.getMaxDrawdown(), 1e-6);
        assertEquals(0, out.getTotalTrades());
        assertEquals(0, out.getWinningTrades());
        assertEquals(0, out.getLosingTrades());
        assertEquals(0.0, out.getWinRate(), 1e-6);
        assertEquals(0.0, out.getTotalReturnRate(), 1e-6);
        assertEquals("No trades", out.getTradingSpanString());
        assertNull(out.getEarliestTrade());
        assertNull(out.getLatestTrade());
    }

    @Test
    void calculateStatistics_mixedTrades_computesCorrectly() {
        LocalDateTime t1 = LocalDateTime.of(2020, 1, 1, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2020, 1, 2, 11, 0);
        LocalDateTime t3 = LocalDateTime.of(2020, 1, 3, 12, 0);

        SimulatedTradeRecord trade1 = new SimulatedTradeRecord("A", true, 1, 10.0, 11.0, 100.0, t1, t1.plusHours(1), "u");
        SimulatedTradeRecord trade2 = new SimulatedTradeRecord("B", true, 1, 20.0, 20.0, 0.0, t2, t2.plusHours(1), "u");
        SimulatedTradeRecord trade3 = new SimulatedTradeRecord("C", false, 1, 30.0, 31.0, -50.0, t3, t3.plusHours(1), "u");

        List<SimulatedTradeRecord> trades = List.of(trade1, trade2, trade3);
        double initialBalance = 1000.0;

        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();
        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(trades, initialBalance);
        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        assertEquals(50.0, out.getTotalProfit(), 1e-6);
        assertEquals(100.0, out.getMaxGain(), 1e-6);
        assertEquals(50.0, out.getMaxDrawdown(), 1e-6);
        assertEquals(3, out.getTotalTrades());
        assertEquals(1, out.getWinningTrades());
        assertEquals(1, out.getLosingTrades());
        assertEquals((1.0 / 3.0) * 100.0, out.getWinRate(), 1e-6);
        assertEquals((50.0 / initialBalance) * 100.0, out.getTotalReturnRate(), 1e-6);
        assertEquals(t1, out.getEarliestTrade());
        assertEquals(t3, out.getLatestTrade());
        assertEquals("2020-01-01 to 2020-01-03", out.getTradingSpanString());
    }

    @Test
    void requestPortfolioSummary_usesGateways_andPresentsOutput() {
        UUID userId = UUID.randomUUID();

        PortfolioTradeGateway tradeGateway = new PortfolioTradeGateway() {
            @Override
            public java.util.List<SimulatedTradeRecord> fetchTradesForUser(UUID u) {
                assertEquals(userId, u);
                LocalDateTime t = LocalDateTime.of(2021, 5, 5, 9, 0);
                SimulatedTradeRecord tr = new SimulatedTradeRecord("X", true, 1, 10.0, 11.0, 10.0, t, t.plusHours(1), u.toString());
                return List.of(tr);
            }
        };

        PortfolioBalanceGateway balanceGateway = new PortfolioBalanceGateway() {
            @Override
            public double getInitialBalance(UUID u) {
                assertEquals(userId, u);
                return 200.0;
            }
        };

        class CapturingOutput implements PortfolioStatisticsOutputBoundary {
            PortfolioStatisticsOutputData captured;

            @Override
            public void present(PortfolioStatisticsOutputData outputData) {
                this.captured = outputData;
            }
        }

        CapturingOutput output = new CapturingOutput();

        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor(tradeGateway, balanceGateway, output);

        interactor.requestPortfolioSummary(userId);

        assertNotNull(output.captured);
        assertEquals(10.0, output.captured.getTotalProfit(), 1e-6);
        assertEquals(10.0, output.captured.getMaxGain(), 1e-6);
        assertEquals(0.0, output.captured.getMaxDrawdown(), 1e-6);
        assertEquals(1, output.captured.getTotalTrades());
        assertEquals(1, output.captured.getWinningTrades());
        assertEquals(0, output.captured.getLosingTrades());
        assertEquals(100.0, output.captured.getWinRate(), 1e-6);
        assertEquals((10.0 / 200.0) * 100.0, output.captured.getTotalReturnRate(), 1e-6);
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

        assertEquals(60.0, out.getTotalProfit(), 1e-6);
        assertEquals(50.0, out.getMaxGain(), 1e-6);
        assertEquals(3, out.getTotalTrades());
        assertEquals(2, out.getWinningTrades());
        assertEquals(1, out.getLosingTrades());
        assertEquals((2.0/3.0)*100.0, out.getWinRate(), 1e-6);
        assertEquals(t1, out.getEarliestTrade());
        assertEquals(t3, out.getLatestTrade());
        assertEquals(6.0, out.getTotalReturnRate(), 1e-6);
    }

    @Test
    void calculateStatistics_allNegativeTrades_noMaxGain_and_zeroMaxDrawdownHandled() {
        LocalDateTime t1 = LocalDateTime.of(2022, 2, 2, 9, 0);
        SimulatedTradeRecord r1 = new SimulatedTradeRecord("AAA", true, 1, 10.0, 9.0, -10.0, t1, t1.plusMinutes(1), "u");
        SimulatedTradeRecord r2 = new SimulatedTradeRecord("BBB", true, 1, 20.0, 19.0, -5.0, t1.plusDays(1), t1.plusDays(1).plusMinutes(1), "u");

        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(List.of(r1, r2), 500.0);
        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();

        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        // totalProfit = -15
        assertEquals(-15.0, out.getTotalProfit(), 1e-6);
        // no positive trades -> maxGain should be 0
        assertEquals(0.0, out.getMaxGain(), 1e-6);
        // maxDrawdown is absolute of min negative (-10) => 10
        assertEquals(10.0, out.getMaxDrawdown(), 1e-6);
        // totalReturnRate = (totalProfit / initialBalance) * 100 = -15/500*100 = -3.0
        assertEquals(-3.0, out.getTotalReturnRate(), 1e-6);
    }

    @Test
    void calculateStatistics_allPositiveTrades_noMaxDrawdown_and_zeroInitialBalanceHandled() {
        LocalDateTime t1 = LocalDateTime.of(2022, 3, 3, 9, 0);
        SimulatedTradeRecord r1 = new SimulatedTradeRecord("CCC", true, 1, 10.0, 12.0, 20.0, t1, t1.plusMinutes(1), "u");
        SimulatedTradeRecord r2 = new SimulatedTradeRecord("DDD", true, 1, 30.0, 33.0, 30.0, t1.plusDays(1), t1.plusDays(1).plusMinutes(1), "u");

        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(List.of(r1, r2), 0.0);
        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();

        PortfolioStatisticsOutputData out = interactor.calculateStatistics(input);

        // totalProfit = 50
        assertEquals(50.0, out.getTotalProfit(), 1e-6);
        // maxGain should be 30
        assertEquals(30.0, out.getMaxGain(), 1e-6);
        // no negative trades -> maxDrawdown should be 0
        assertEquals(0.0, out.getMaxDrawdown(), 1e-6);
        // initialBalance == 0 should make totalReturnRate = 0 (guarded in code)
        assertEquals(0.0, out.getTotalReturnRate(), 1e-6);
    }

    @Test
    void calculateStatistics_nullTrades_fromCustomInput_returnsZeroedOutput() {
        // Create a subclass that returns null for getTrades() to exercise the null branch
        PortfolioStatisticsInputData custom = new PortfolioStatisticsInputData(List.of(), 100.0) {
            @Override
            public java.util.List<SimulatedTradeRecord> getTrades() {
                return null;
            }
        };

        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor();
        PortfolioStatisticsOutputData out = interactor.calculateStatistics(custom);

        assertEquals(0.0, out.getTotalProfit(), 1e-6);
        assertEquals(0, out.getTotalTrades());
        assertEquals("No trades", out.getTradingSpanString());
    }

    @Test
    void inputData_nullTradesConstructor_coversNullBranch() {
        // Pass null directly to constructor to hit the if(tempTrades == null) branch
        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(null, 500.0);

        // trades should be replaced with empty list
        assertNotNull(input.getTrades());
        assertTrue(input.getTrades().isEmpty());
        assertEquals(500.0, input.getInitialBalance(), 1e-6);
    }

    @Test
    void outputData_getTradingSpanString_onlyEarliestNull_returnsNoTrades() {
        // earliestTrade is null, latestTrade is not
        PortfolioStatisticsOutputData out = new PortfolioStatisticsOutputData(
            0, 0, 0, 0, 0, 0, 0, 0,
            null, LocalDateTime.of(2020, 1, 1, 10, 0)
        );
        assertEquals("No trades", out.getTradingSpanString());
    }

    @Test
    void outputData_getTradingSpanString_onlyLatestNull_returnsNoTrades() {
        // earliestTrade is not null, latestTrade is null
        PortfolioStatisticsOutputData out = new PortfolioStatisticsOutputData(
            0, 0, 0, 0, 0, 0, 0, 0,
            LocalDateTime.of(2020, 1, 1, 10, 0), null
        );
        assertEquals("No trades", out.getTradingSpanString());
    }
}
