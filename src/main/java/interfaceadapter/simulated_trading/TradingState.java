package interfaceadapter.simulated_trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Position;

/**
 * Represents all data displayed in the trading view.
 */
public class TradingState {

    // --- Constants for avoiding repeated literals ---
    private static final String DEFAULT_MONEY = "$0.00";
    private static final String DEFAULT_RATE = "0.00%";
    private static final String DEFAULT_LOADING = "Loading...";
    private static final String DEFAULT_TICKER = "AAPL";
    private static final String ZERO_STRING = "0";

    // --- Top-level displayed values ---
    private String availableCash = "$100,000.00";
    private String currentPrice = DEFAULT_LOADING;
    private List<Double> chartData = new ArrayList<>();

    // --- Summary statistics ---
    private String totalProfit = DEFAULT_MONEY;
    private String totalReturnRate = DEFAULT_RATE;
    private String maxDrawdown = DEFAULT_MONEY;
    private String maxGain = DEFAULT_MONEY;
    private String totalTrades = ZERO_STRING;
    private String winningTrades = ZERO_STRING;
    private String losingTrades = ZERO_STRING;
    private String winRate = DEFAULT_RATE;

    // --- Positions and errors ---
    private Map<String, Position> positions = new HashMap<>();
    private String error;

    /**
     * Constructs a default TradingState.
     */
    public TradingState() {
    }

    /**
     * Returns the available cash balance.
     *
     * @return the available cash balance
     */
    public String getAvailableCash() {
        return availableCash;
    }

    /**
     * Sets the available cash balance.
     *
     * @param availableCash the available cash value
     */
    public void setAvailableCash(String availableCash) {
        this.availableCash = availableCash;
    }

    /**
     * Returns the ticker symbol currently shown.
     *
     * @return the ticker symbol
     */
    public String getTicker() {
        return DEFAULT_TICKER;
    }

    /**
     * Returns the current formatted price.
     *
     * @return the formatted current price
     */
    public String getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Sets the formatted current price.
     *
     * @param currentPrice the formatted price
     */
    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    /**
     * Returns the list of chart data points.
     *
     * @return the chart data list
     */
    public List<Double> getChartData() {
        return chartData;
    }

    /**
     * Sets the chart data list.
     *
     * @param chartData the new chart data list
     */
    public void setChartData(List<Double> chartData) {
        this.chartData = chartData;
    }

    /**
     * Returns the formatted total profit.
     *
     * @return the formatted total profit
     */
    public String getTotalProfit() {
        return totalProfit;
    }

    /**
     * Sets the formatted total profit.
     *
     * @param totalProfit the formatted total profit
     */
    public void setTotalProfit(String totalProfit) {
        this.totalProfit = totalProfit;
    }

    /**
     * Returns the formatted return rate.
     *
     * @return the formatted return rate
     */
    public String getTotalReturnRate() {
        return totalReturnRate;
    }

    /**
     * Sets the formatted return rate.
     *
     * @param totalReturnRate the formatted return rate
     */
    public void setTotalReturnRate(String totalReturnRate) {
        this.totalReturnRate = totalReturnRate;
    }

    /**
     * Returns the maximum drawdown.
     *
     * @return the maximum drawdown
     */
    public String getMaxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Sets the maximum drawdown.
     *
     * @param maxDrawdown the maximum drawdown
     */
    public void setMaxDrawdown(String maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    /**
     * Returns the maximum single gain.
     *
     * @return the maximum single gain
     */
    public String getMaxGain() {
        return maxGain;
    }

    /**
     * Sets the maximum single gain.
     *
     * @param maxGain the maximum single gain
     */
    public void setMaxGain(String maxGain) {
        this.maxGain = maxGain;
    }

    /**
     * Returns the total number of trades executed.
     *
     * @return the number of executed trades
     */
    public String getTotalTrades() {
        return totalTrades;
    }

    /**
     * Sets the total number of trades executed.
     *
     * @param totalTrades the number of executed trades
     */
    public void setTotalTrades(String totalTrades) {
        this.totalTrades = totalTrades;
    }

    /**
     * Returns the number of winning trades.
     *
     * @return the number of winning trades
     */
    public String getWinningTrades() {
        return winningTrades;
    }

    /**
     * Sets the number of winning trades.
     *
     * @param winningTrades the number of winning trades
     */
    public void setWinningTrades(String winningTrades) {
        this.winningTrades = winningTrades;
    }

    /**
     * Returns the number of losing trades.
     *
     * @return the number of losing trades
     */
    public String getLosingTrades() {
        return losingTrades;
    }

    /**
     * Sets the number of losing trades.
     *
     * @param losingTrades the number of losing trades
     */
    public void setLosingTrades(String losingTrades) {
        this.losingTrades = losingTrades;
    }

    /**
     * Returns the formatted win rate.
     *
     * @return the formatted win rate
     */
    public String getWinRate() {
        return winRate;
    }

    /**
     * Sets the formatted win rate.
     *
     * @param winRate the formatted win rate
     */
    public void setWinRate(String winRate) {
        this.winRate = winRate;
    }

    /**
     * Returns the map of positions held.
     *
     * @return the positions map
     */
    public Map<String, Position> getPositions() {
        return positions;
    }

    /**
     * Sets the map of positions held.
     *
     * @param positions the map of positions
     */
    public void setPositions(Map<String, Position> positions) {
        this.positions = positions;
    }

    /**
     * Returns the latest error message.
     *
     * @return the error message
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the latest error message.
     *
     * @param error the error message
     */
    public void setError(String error) {
        this.error = error;
    }
}
