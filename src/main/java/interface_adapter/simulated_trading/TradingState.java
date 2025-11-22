package interface_adapter.simulated_trading;

import entity.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradingState {
    // 1. Top Info
    private String availableCash = "$100,000.00";

    // 2. Chart Area
    private String ticker = "AAPL";
    private String currentPrice = "Loading...";
    private List<Double> chartData = new ArrayList<>(); // For drawing the graph

    // 3. Portfolio Summary (Bottom)
    private String totalProfit = "$0.00";
    private String totalReturnRate = "0.00%";
    private String maxDrawdown = "$0.00";
    private String maxGain = "$0.00";
    private String totalTrades = "0";
    private String winRate = "0%";

    // 4. Wallet Table (Holdings)
    private Map<String, Position> positions = new HashMap<>();

    // 5. Errors (For popups)
    private String error = null;

    // Copy Constructor (Required for Clean Arch to keep state immutable)
    public TradingState(TradingState copy) {
        this.availableCash = copy.availableCash;
        this.ticker = copy.ticker;
        this.currentPrice = copy.currentPrice;
        this.chartData = new ArrayList<>(copy.chartData);
        this.totalProfit = copy.totalProfit;
        this.totalReturnRate = copy.totalReturnRate;
        this.maxDrawdown = copy.maxDrawdown;
        this.maxGain = copy.maxGain;
        this.totalTrades = copy.totalTrades;
        this.winRate = copy.winRate;
        this.positions = new HashMap<>(copy.positions);
        this.error = copy.error;
    }

    // Default Constructor
    public TradingState() {}

    // --- Getters & Setters ---

    public String getAvailableCash() { return availableCash; }
    public void setAvailableCash(String availableCash) { this.availableCash = availableCash; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(String currentPrice) { this.currentPrice = currentPrice; }

    public List<Double> getChartData() { return chartData; }
    public void setChartData(List<Double> chartData) { this.chartData = chartData; }

    public String getTotalProfit() { return totalProfit; }
    public void setTotalProfit(String totalProfit) { this.totalProfit = totalProfit; }

    public String getTotalReturnRate() { return totalReturnRate; }
    public void setTotalReturnRate(String totalReturnRate) { this.totalReturnRate = totalReturnRate; }

    public String getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(String maxDrawdown) { this.maxDrawdown = maxDrawdown; }

    public String getMaxGain() { return maxGain; }
    public void setMaxGain(String maxGain) { this.maxGain = maxGain; }

    public String getTotalTrades() { return totalTrades; }
    public void setTotalTrades(String totalTrades) { this.totalTrades = totalTrades; }

    public String getWinRate() { return winRate; }
    public void setWinRate(String winRate) { this.winRate = winRate; }

    public Map<String, Position> getPositions() { return positions; }
    public void setPositions(Map<String, Position> positions) { this.positions = positions; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}