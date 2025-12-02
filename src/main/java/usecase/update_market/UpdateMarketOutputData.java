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
public record UpdateMarketOutputData(double currentPrice, double totalEquity, double totalReturnRate,
                                     double maxDrawdown, double availableCash, int totalTrades, int winningTrades,
                                     double maxGain, int losingTrades, double winRate, List<Double> chartData,
                                     Map<String, Position> positions) {
    /**
     * Constructs the output data for the market update.
     *
     * @param currentPrice    the latest market price
     * @param totalEquity     the updated total equity value
     * @param totalReturnRate the overall return rate
     * @param maxDrawdown     the maximum drawdown
     * @param availableCash   available cash balance
     * @param totalTrades     total closed trades
     * @param winningTrades   number of winning trades
     * @param maxGain         highest realized gain
     * @param losingTrades    number of losing trades
     * @param winRate         win rate across trades
     * @param chartData       price history data for charts
     * @param positions       open positions indexed by ticker
     */
    public UpdateMarketOutputData {

    }

    /**
     * Returns the latest market price.
     *
     * @return latest market price
     */
    @Override
    public double currentPrice() {
        return currentPrice;
    }

    /**
     * Returns the updated total equity value.
     *
     * @return total equity
     */
    @Override
    public double totalEquity() {
        return totalEquity;
    }

    /**
     * Returns the user's overall return rate.
     *
     * @return return rate
     */
    @Override
    public double totalReturnRate() {
        return totalReturnRate;
    }

    /**
     * Returns the user's maximum drawdown.
     *
     * @return max drawdown
     */
    @Override
    public double maxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Returns the available cash balance.
     *
     * @return available cash
     */
    @Override
    public double availableCash() {
        return availableCash;
    }

    /**
     * Returns the total number of closed trades.
     *
     * @return total closed trades
     */
    @Override
    public int totalTrades() {
        return totalTrades;
    }

    /**
     * Returns the number of winning trades.
     *
     * @return winning trades
     */
    @Override
    public int winningTrades() {
        return winningTrades;
    }

    /**
     * Returns the highest realized gain.
     *
     * @return maximum gain
     */
    @Override
    public double maxGain() {
        return maxGain;
    }

    /**
     * Returns the number of losing trades.
     *
     * @return losing trade count
     */
    @Override
    public int losingTrades() {
        return losingTrades;
    }

    /**
     * Returns the win rate across all trades.
     *
     * @return win rate
     */
    @Override
    public double winRate() {
        return winRate;
    }

    /**
     * Returns the price history used for chart display.
     *
     * @return chart data
     */
    @Override
    public List<Double> chartData() {
        return chartData;
    }

    /**
     * Returns the open positions indexed by ticker.
     *
     * @return open positions
     */
    @Override
    public Map<String, Position> positions() {
        return positions;
    }

}
