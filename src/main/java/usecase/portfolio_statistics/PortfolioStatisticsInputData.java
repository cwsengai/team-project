package usecase.portfolio_statistics;

import java.util.List;

import entity.SimulatedTradeRecord;

/**
 * Input data for the Portfolio Statistics use case.
 * Contains the user's trade history and initial balance used to calculate statistics.
 * 
 * @param trades         a list of completed simulated trades; if null, an empty list is used
 * @param initialBalance the initial account balance of the user
 */
public record PortfolioStatisticsInputData(List<SimulatedTradeRecord> trades, double initialBalance) {

    /**
     * Creates a new PortfolioStatisticsInputData instance.
     *
     * @param trades         a list of completed simulated trades; if null, an empty list is used
     * @param initialBalance the initial account balance of the user
     */
    public PortfolioStatisticsInputData(List<SimulatedTradeRecord> trades,
                                        double initialBalance) {

        List<SimulatedTradeRecord> tempTrades = trades;

        if (tempTrades == null) {
            tempTrades = List.of();
        }

        this.trades = tempTrades;
        this.initialBalance = initialBalance;
    }

    /**
     * Returns the list of simulated trades used for portfolio statistics.
     *
     * @return the list of trade records
     */
    @Override
    public List<SimulatedTradeRecord> trades() {
        return trades;
    }

    /**
     * Returns the initial balance of the portfolio.
     *
     * @return the initial balance value
     */
    @Override
    public double initialBalance() {
        return initialBalance;
    }
}
