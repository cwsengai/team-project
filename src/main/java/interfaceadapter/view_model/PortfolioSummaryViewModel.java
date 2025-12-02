package interfaceadapter.view_model;

import java.time.LocalDateTime;

public record PortfolioSummaryViewModel(double totalProfit, double maxGain, double maxDrawdown, int tradeCount,
                                        int winningTrades, int losingTrades, double winRate, double totalReturnRate,
                                        LocalDateTime earliest, LocalDateTime latest) {
}
