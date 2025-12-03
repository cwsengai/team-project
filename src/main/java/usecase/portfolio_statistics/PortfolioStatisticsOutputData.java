package usecase.portfolio_statistics;

import java.time.LocalDateTime;

/**
 * Output data object representing calculated portfolio statistics.
 * Contains profit metrics, trade counts, return rates, and trading time span.
 */
public class PortfolioStatisticsOutputData {

    private final double totalProfit;
    private final double maxGain;
    private final double maxDrawdown;
    private final int totalTrades;
    private final int winningTrades;
    private final int losingTrades;
    private final double winRate;
    private final double totalReturnRate;
    private final LocalDateTime earliestTrade;
    private final LocalDateTime latestTrade;

    /**
     * Creates a new PortfolioStatisticsOutputData instance.
     *
     * @param totalProfit the total profit across all trades
     * @param maxGain the maximum single-trade positive return
     * @param maxDrawdown the maximum single-trade drawdown
     * @param totalTrades the total number of trades executed
     * @param winningTrades the number of trades with positive PnL
     * @param losingTrades the number of trades with negative PnL
     * @param winRate the percentage of trades that were profitable
     * @param totalReturnRate the overall portfolio return rate
     * @param earliestTrade the earliest trade timestamp
     * @param latestTrade the latest trade timestamp
     */
    public PortfolioStatisticsOutputData(double totalProfit, double maxGain, double maxDrawdown,
                                         int totalTrades, int winningTrades, int losingTrades,
                                         double winRate, double totalReturnRate,
                                         LocalDateTime earliestTrade, LocalDateTime latestTrade) {
        this.totalProfit = totalProfit;
        this.maxGain = maxGain;
        this.maxDrawdown = maxDrawdown;
        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.losingTrades = losingTrades;
        this.winRate = winRate;
        this.totalReturnRate = totalReturnRate;
        this.earliestTrade = earliestTrade;
        this.latestTrade = latestTrade;
    }

    /**
     * Returns the total profit across all trades.
     *
     * @return the total profit value
     */
    public double getTotalProfit() {
        return totalProfit;
    }

    /**
     * Returns the maximum gain recorded in any trade.
     *
     * @return the maximum gain value
     */
    public double getMaxGain() {
        return maxGain;
    }

    /**
     * Returns the maximum drawdown recorded in any trade.
     *
     * @return the maximum drawdown value
     */
    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Returns the total number of trades executed.
     *
     * @return the total trade count
     */
    public int getTotalTrades() {
        return totalTrades;
    }

    /**
     * Returns the number of profitable trades.
     *
     * @return the count of winning trades
     */
    public int getWinningTrades() {
        return winningTrades;
    }

    /**
     * Returns the number of unprofitable trades.
     *
     * @return the count of losing trades
     */
    public int getLosingTrades() {
        return losingTrades;
    }

    /**
     * Returns the percentage of trades that were profitable.
     *
     * @return the win rate as a percentage
     */
    public double getWinRate() {
        return winRate;
    }

    /**
     * Returns the total return rate of the portfolio.
     *
     * @return the total return percentage
     */
    public double getTotalReturnRate() {
        return totalReturnRate;
    }

    /**
     * Returns the earliest executed trade timestamp.
     *
     * @return the earliest trade time
     */
    public LocalDateTime getEarliestTrade() {
        return earliestTrade;
    }

    /**
     * Returns the latest executed trade timestamp.
     *
     * @return the latest trade time
     */
    public LocalDateTime getLatestTrade() {
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
