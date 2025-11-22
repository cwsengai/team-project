package use_case.update_market;

import java.util.List;

public class UpdateMarketOutputData {
    private final double currentPrice;
    private final double totalEquity;
    private final double totalReturnRate;
    private final double maxDrawdown;
    private final double availableCash;
    private final List<Double> chartData;
    private final String error;

    public UpdateMarketOutputData(double currentPrice, double totalEquity,
                                  double totalReturnRate, double maxDrawdown,
                                  double availableCash, List<Double> chartData, String error) {
        this.currentPrice = currentPrice;
        this.totalEquity = totalEquity;
        this.totalReturnRate = totalReturnRate;
        this.maxDrawdown = maxDrawdown;
        this.availableCash = availableCash;
        this.chartData = chartData;
        this.error = error;
    }

    public double getCurrentPrice() { return currentPrice; }
    public double getTotalEquity() { return totalEquity; }
    public double getTotalReturnRate() { return totalReturnRate; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public double getAvailableCash() { return availableCash; }
    public List<Double> getChartData() { return chartData; }
    public String getError() { return error; }
}