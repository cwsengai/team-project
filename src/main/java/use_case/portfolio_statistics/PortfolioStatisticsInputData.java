package use_case.portfolio_statistics;

import java.util.List;

import entity.SimulatedTradeRecord;

public class PortfolioStatisticsInputData {
    private final List<SimulatedTradeRecord> trades;
    private final double initialBalance;

    public PortfolioStatisticsInputData(List<SimulatedTradeRecord> trades, double initialBalance) {
        this.trades = trades != null ? trades : List.of();
        this.initialBalance = initialBalance;
    }

    public List<SimulatedTradeRecord> getTrades() {
        return trades;
    }

    public double getInitialBalance() {
        return initialBalance;
    }
}