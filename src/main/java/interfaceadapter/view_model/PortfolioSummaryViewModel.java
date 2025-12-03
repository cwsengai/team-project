package interfaceadapter.view_model;

import java.time.LocalDateTime;

public class PortfolioSummaryViewModel {
    public final double totalProfit;
    public final double maxGain;
    public final double maxDrawdown;
    public final int tradeCount;
    public final int winningTrades;
    public final int losingTrades;
    public final double winRate;
    public final double totalReturnRate;
    public final LocalDateTime earliest;
    public final LocalDateTime latest;

    public PortfolioSummaryViewModel(double totalProfit, double maxGain, double maxDrawdown,
                                     int tradeCount, int winningTrades, int losingTrades,
                                     double winRate, double totalReturnRate,
                                     LocalDateTime earliest, LocalDateTime latest) {
        this.totalProfit = totalProfit;
        this.maxGain = maxGain;
        this.maxDrawdown = maxDrawdown;
        this.tradeCount = tradeCount;
        this.winningTrades = winningTrades;
        this.losingTrades = losingTrades;
        this.winRate = winRate;
        this.totalReturnRate = totalReturnRate;
        this.earliest = earliest;
        this.latest = latest;
    }
}
