package usecase.update_market;

import java.util.List;
import java.util.Map;

import entity.Position;

/**
 * Output data for the UpdateMarket use case.
 *
 * <p>This object stores updated trading statistics, equity values,
 * price history, open positions, and any error message returned
 * by the interactor.</p>
 */
public class UpdateMarketOutputData {
    private final double currentPrice;
    private final double totalEquity;
    private final double totalReturnRate;
    private final double maxDrawdown;
    private final double availableCash;

    private final int totalTrades;
    private final int winningTrades;
    private final double maxGain;
    private final int losingTrades;
    private final double winRate;

    private final List<Double> chartData;
    private final Map<String, Position> positions;

    /**
     * Constructs the output data for the market update.
     *
     * @param currentPrice the latest market price
     * @param totalEquity the updated total equity value
     * @param totalReturnRate the overall return rate
     * @param maxDrawdown the maximum drawdown
     * @param availableCash available cash balance
     * @param totalTrades total closed trades
     * @param winningTrades number of winning trades
     * @param maxGain highest realized gain
     * @param losingTrades number of losing trades
     * @param winRate win rate across trades
     * @param chartData price history data for charts
     * @param positions open positions indexed by ticker
     * @param error error message or null if none
     */
    public UpdateMarketOutputData(double currentPrice, double totalEquity, double totalReturnRate,
                                  double maxDrawdown, double availableCash, int totalTrades,
                                  int winningTrades, double maxGain, int losingTrades,
                                  double winRate, List<Double> chartData,
                                  Map<String, Position> positions, String error) {

        this.currentPrice = currentPrice;
        this.totalEquity = totalEquity;
        this.totalReturnRate = totalReturnRate;
        this.maxDrawdown = maxDrawdown;
        this.availableCash = availableCash;

        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.maxGain = maxGain;
        this.losingTrades = losingTrades;
        this.winRate = winRate;

        this.chartData = chartData;
        this.positions = positions;
    }

    /**
     * Returns the latest market price.
     *
     * @return latest market price
     */
    public double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Returns the updated total equity value.
     *
     * @return total equity
     */
    public double getTotalEquity() {
        return totalEquity;
    }

    /**
     * Returns the user's overall return rate.
     *
     * @return return rate
     */
    public double getTotalReturnRate() {
        return totalReturnRate;
    }

    /**
     * Returns the user's maximum drawdown.
     *
     * @return max drawdown
     */
    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Returns the available cash balance.
     *
     * @return available cash
     */
    public double getAvailableCash() {
        return availableCash;
    }

    /**
     * Returns the total number of closed trades.
     *
     * @return total closed trades
     */
    public int getTotalTrades() {
        return totalTrades;
    }

    /**
     * Returns the number of winning trades.
     *
     * @return winning trades
     */
    public int getWinningTrades() {
        return winningTrades;
    }

    /**
     * Returns the highest realized gain.
     *
     * @return maximum gain
     */
    public double getMaxGain() {
        return maxGain;
    }

    /**
     * Returns the number of losing trades.
     *
     * @return losing trade count
     */
    public int getLosingTrades() {
        return losingTrades;
    }

    /**
     * Returns the win rate across all trades.
     *
     * @return win rate
     */
    public double getWinRate() {
        return winRate;
    }

    /**
     * Returns the price history used for chart display.
     *
     * @return chart data
     */
    public List<Double> getChartData() {
        return chartData;
    }

    /**
     * Returns the open positions indexed by ticker.
     *
     * @return open positions
     */
    public Map<String, Position> getPositions() {
        return positions;
    }

}
