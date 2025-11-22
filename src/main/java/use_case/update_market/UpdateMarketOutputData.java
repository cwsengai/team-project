package use_case.update_market;

import entity.Position;
import java.util.List;
import java.util.Map;

public class UpdateMarketOutputData {
    private final double currentPrice;
    private final double totalEquity;
    private final double totalReturnRate;
    private final double maxDrawdown;
    private final double availableCash;
    private final List<Double> chartData;
    private final Map<String, Position> positions;
    private final String error;

    public UpdateMarketOutputData(double currentPrice, double totalEquity,
                                  double totalReturnRate, double maxDrawdown,
                                  double availableCash, List<Double> chartData,
                                  Map<String, Position> positions, String error) { // 👈 必须添加这个参数
        this.currentPrice = currentPrice;
        this.totalEquity = totalEquity;
        this.totalReturnRate = totalReturnRate;
        this.maxDrawdown = maxDrawdown;
        this.availableCash = availableCash;
        this.chartData = chartData;
        this.positions = positions;
        this.error = error;
    }

    public double getCurrentPrice() { return currentPrice; }
    public double getTotalEquity() { return totalEquity; }
    public double getTotalReturnRate() { return totalReturnRate; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public double getAvailableCash() { return availableCash; }
    public List<Double> getChartData() { return chartData; }
    public String getError() { return error; }
    public Map<String, Position> getPositions() { return positions; }
}