package use_case.portfolio_statistics;

import java.util.List;

import entity.SimulatedTradeRecord;

public class PortfolioStatisticsInputData {
    private final List<SimulatedTradeRecord> trades;

    public PortfolioStatisticsInputData(List<SimulatedTradeRecord> trades) {
        this.trades = trades != null ? trades : List.of();
    }

    public List<SimulatedTradeRecord> getTrades() {
        return trades;
    }
}