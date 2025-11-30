package usecase.portfolio_statistics;

import java.time.LocalDateTime;
import java.util.List;

import entity.SimulatedTradeRecord;

public class PortfolioStatisticsInteractor {

    public PortfolioStatisticsOutputData calculateStatistics(PortfolioStatisticsInputData input) {
        List<SimulatedTradeRecord> trades = input.getTrades();
        double initialBalance = input.getInitialBalance();

        if (trades.isEmpty()) {
            return new PortfolioStatisticsOutputData(0, 0, 0, 0, 0, 0, 0, 0, null, null);
        }

        // Calculate total profit
        double totalProfit = trades.stream()
            .mapToDouble(SimulatedTradeRecord::getRealizedPnL)
            .sum();

        // Calculate max gain (highest positive PnL)
        double maxGain = trades.stream()
            .mapToDouble(SimulatedTradeRecord::getRealizedPnL)
            .filter(pnl -> pnl > 0)
            .max()
            .orElse(0.0);

        // Calculate max drawdown (most negative PnL, but show as positive for display)
        double maxDrawdownValue = trades.stream()
            .mapToDouble(SimulatedTradeRecord::getRealizedPnL)
            .filter(pnl -> pnl < 0)
            .min()
            .orElse(0.0);
        double maxDrawdown = Math.abs(maxDrawdownValue); // Show as positive

        // Count winning and losing trades
        int winningTrades = (int) trades.stream()
            .filter(t -> t.getRealizedPnL() > 0)
            .count();

        int losingTrades = (int) trades.stream()
            .filter(t -> t.getRealizedPnL() < 0)
            .count();

        // Calculate win rate
        double winRate = trades.isEmpty() ? 0 : (double) winningTrades / trades.size() * 100;

        // Calculate total return rate
        double totalReturnRate = initialBalance > 0 ? (totalProfit / initialBalance) * 100 : 0;

        // Find trading span
        LocalDateTime earliest = trades.stream()
            .map(SimulatedTradeRecord::getEntryTime)
            .min(LocalDateTime::compareTo)
            .orElse(null);

        LocalDateTime latest = trades.stream()
            .map(SimulatedTradeRecord::getEntryTime)
            .max(LocalDateTime::compareTo)
            .orElse(null);

        return new PortfolioStatisticsOutputData(
            totalProfit, maxGain, maxDrawdown, trades.size(),
            winningTrades, losingTrades, winRate, totalReturnRate, earliest, latest
        );
    }
}