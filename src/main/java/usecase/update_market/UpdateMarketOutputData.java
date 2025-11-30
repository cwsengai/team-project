package usecase.update_market;

import entity.Position;
import java.util.List;
import java.util.Map;

public class UpdateMarketOutputData {
    private final double currentPrice;
    private final double totalEquity;
    private final double totalReturnRate;
    private final double maxDrawdown;
    private final double availableCash;

    // --- Stats (Raw Counts and Calculated Values) ---
    private final int totalTrades;
    private final int winningTrades;
    private final double maxGain;
    private final int losingTrades;
    private final double winRate;
    // ---

    private final List<Double> chartData;
    private final Map<String, Position> positions;
    private final String error;

    // Must match the order of parameters passed by the Interactor
    public UpdateMarketOutputData(double currentPrice, double totalEquity, double totalReturnRate, double maxDrawdown,
                                  double availableCash, int totalTrades, int winningTrades, double maxGain,
                                  int losingTrades, double winRate, List<Double> chartData, Map<String,
                    Position> positions, String error) {
        this.currentPrice = currentPrice;
        this.totalEquity = totalEquity;
        this.totalReturnRate = totalReturnRate;
        this.maxDrawdown = maxDrawdown;
        this.availableCash = availableCash;

        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.maxGain = maxGain;
        this.losingTrades = losingTrades;
        this.winRate = winRate; // Assignment

        this.chartData = chartData;
        this.positions = positions;
        this.error = error;
    }

    // --- Accessors ---
    public double getCurrentPrice() { return currentPrice; }
    public double getTotalEquity() { return totalEquity; }
    public double getTotalReturnRate() { return totalReturnRate; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public double getAvailableCash() { return availableCash; }

    public int getTotalTrades() { return totalTrades; }
    public int getWinningTrades() { return winningTrades; }
    public double getMaxGain() { return maxGain; }
    public int getLosingTrades() { return losingTrades; }
    public double getWinRate() { return winRate; }

    public List<Double> getChartData() { return chartData; }
    public Map<String, Position> getPositions() { return positions; }
    public String getError() { return error; }
}