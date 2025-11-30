package use_case.portfolio_statistics;

import java.time.LocalDateTime;

public class PortfolioStatisticsOutputData {
    private final double totalProfit;
    private final double maxGain;
    private final double maxDrawdown;
    private final int totalTrades;
    private final int winningTrades;
    private final int losingTrades;
    private final double winRate;
    private final LocalDateTime earliestTrade;
    private final LocalDateTime latestTrade;

    public PortfolioStatisticsOutputData(double totalProfit, double maxGain, double maxDrawdown,
                                       int totalTrades, int winningTrades, int losingTrades,
                                       double winRate, LocalDateTime earliestTrade, LocalDateTime latestTrade) {
        this.totalProfit = totalProfit;
        this.maxGain = maxGain;
        this.maxDrawdown = maxDrawdown;
        this.totalTrades = totalTrades;
        this.winningTrades = winningTrades;
        this.losingTrades = losingTrades;
        this.winRate = winRate;
        this.earliestTrade = earliestTrade;
        this.latestTrade = latestTrade;
    }

    public double getTotalProfit() { return totalProfit; }
    public double getMaxGain() { return maxGain; }
    public double getMaxDrawdown() { return maxDrawdown; }
    public int getTotalTrades() { return totalTrades; }
    public int getWinningTrades() { return winningTrades; }
    public int getLosingTrades() { return losingTrades; }
    public double getWinRate() { return winRate; }
    public LocalDateTime getEarliestTrade() { return earliestTrade; }
    public LocalDateTime getLatestTrade() { return latestTrade; }

    public String getTradingSpanString() {
        if (earliestTrade == null || latestTrade == null) {
            return "No trades";
        }
        return earliestTrade.toLocalDate() + " to " + latestTrade.toLocalDate();
    }
}