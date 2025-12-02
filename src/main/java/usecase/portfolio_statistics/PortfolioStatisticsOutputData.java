package usecase.portfolio_statistics;

import java.time.LocalDateTime;

/**
 * Output data object representing calculated portfolio statistics.
 * Contains profit metrics, trade counts, return rates, and trading time span.
 * 
 * @param totalProfit     the total profit across all trades
 * @param maxGain         the maximum single-trade positive return
 * @param maxDrawdown     the maximum single-trade drawdown
 * @param totalTrades     the total number of trades executed
 * @param winningTrades   the number of trades with positive PnL
 * @param losingTrades    the number of trades with negative PnL
 * @param winRate         the percentage of trades that were profitable
 * @param totalReturnRate the overall portfolio return rate
 * @param earliestTrade   the earliest trade timestamp
 * @param latestTrade     the latest trade timestamp
 */
public record PortfolioStatisticsOutputData(double totalProfit, double maxGain, double maxDrawdown, int totalTrades,
                                            int winningTrades, int losingTrades, double winRate, double totalReturnRate,
                                            LocalDateTime earliestTrade, LocalDateTime latestTrade) {

    /**
     * Creates a new PortfolioStatisticsOutputData instance.
     *
     * @param totalProfit     the total profit across all trades
     * @param maxGain         the maximum single-trade positive return
     * @param maxDrawdown     the maximum single-trade drawdown
     * @param totalTrades     the total number of trades executed
     * @param winningTrades   the number of trades with positive PnL
     * @param losingTrades    the number of trades with negative PnL
     * @param winRate         the percentage of trades that were profitable
     * @param totalReturnRate the overall portfolio return rate
     * @param earliestTrade   the earliest trade timestamp
     * @param latestTrade     the latest trade timestamp
     */
    public PortfolioStatisticsOutputData {
    }

    /**
     * Returns the total profit across all trades.
     *
     * @return the total profit value
     */
    @Override
    public double totalProfit() {
        return totalProfit;
    }

    /**
     * Returns the maximum gain recorded in any trade.
     *
     * @return the maximum gain value
     */
    @Override
    public double maxGain() {
        return maxGain;
    }

    /**
     * Returns the maximum drawdown recorded in any trade.
     *
     * @return the maximum drawdown value
     */
    @Override
    public double maxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Returns the total number of trades executed.
     *
     * @return the total trade count
     */
    @Override
    public int totalTrades() {
        return totalTrades;
    }

    /**
     * Returns the number of profitable trades.
     *
     * @return the count of winning trades
     */
    @Override
    public int winningTrades() {
        return winningTrades;
    }

    /**
     * Returns the number of unprofitable trades.
     *
     * @return the count of losing trades
     */
    @Override
    public int losingTrades() {
        return losingTrades;
    }

    /**
     * Returns the percentage of trades that were profitable.
     *
     * @return the win rate as a percentage
     */
    @Override
    public double winRate() {
        return winRate;
    }

    /**
     * Returns the total return rate of the portfolio.
     *
     * @return the total return percentage
     */
    @Override
    public double totalReturnRate() {
        return totalReturnRate;
    }

    /**
     * Returns the earliest executed trade timestamp.
     *
     * @return the earliest trade time
     */
    @Override
    public LocalDateTime earliestTrade() {
        return earliestTrade;
    }

    /**
     * Returns the latest executed trade timestamp.
     *
     * @return the latest trade time
     */
    @Override
    public LocalDateTime latestTrade() {
        return latestTrade;
    }

    /**
     * Returns a readable string describing the time span of trading activity.
     *
     * @return a formatted date range string, or {@code "No trades"} if none exist
     */
    public String getTradingSpanString() {
        if (earliestTrade == null || latestTrade == null) {
            return "No trades";
        }
        return earliestTrade.toLocalDate() + " to " + latestTrade.toLocalDate();
    }
}
