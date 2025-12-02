package usecase.portfolio_statistics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import entity.SimulatedTradeRecord;

public record PortfolioStatisticsInteractor(PortfolioTradeGateway tradeGateway, PortfolioBalanceGateway balanceGateway,
                                            PortfolioStatisticsOutputBoundary outputBoundary) implements PortfolioStatisticsInputBoundary {

    // Default no-arg constructor for tests and backward compatibility.
    public PortfolioStatisticsInteractor() {
        this(null, null, null);
    }

    /**
     * Handles the request to calculate and present portfolio statistics for a user.
     *
     * @param userId the UUID of the user whose portfolio statistics are requested
     */
    public void requestPortfolioSummary(UUID userId) {
        List<SimulatedTradeRecord> trades = Objects.requireNonNull(tradeGateway).fetchTradesForUser(userId);
        double initialBalance = Objects.requireNonNull(balanceGateway).getInitialBalance(userId);

        PortfolioStatisticsInputData input = new PortfolioStatisticsInputData(trades, initialBalance);
        PortfolioStatisticsOutputData stats = calculateStatistics(input);
        Objects.requireNonNull(outputBoundary).present(stats);
    }

    /**
     * Calculates the portfolio statistics using the user's trades and initial balance.
     * If no trades are provided, returns statistics with zeroed values.
     *
     * @param input the portfolio input data containing trade history and initial balance
     * @return the calculated portfolio statistics
     */
    public PortfolioStatisticsOutputData calculateStatistics(PortfolioStatisticsInputData input) {
        List<SimulatedTradeRecord> trades = input.trades();
        double initialBalance = input.initialBalance();

        if (trades == null || trades.isEmpty()) {
            return new PortfolioStatisticsOutputData(0, 0, 0, 0, 0, 0, 0, 0, null, null);
        }

        return computeStatisticsFromTrades(trades, initialBalance);
    }

    private PortfolioStatisticsOutputData computeStatisticsFromTrades(List<SimulatedTradeRecord> trades,
                                                                      double initialBalance) {
        // Calculate total profit
        double totalProfit = trades.stream()
                .mapToDouble(SimulatedTradeRecord::realizedPnL)
                .sum();

        // Calculate max gain (highest positive PnL)
        double maxGain = trades.stream()
                .mapToDouble(SimulatedTradeRecord::realizedPnL)
                .filter(pnl -> pnl > 0)
                .max()
                .orElse(0.0);

        // Calculate max drawdown (most negative PnL, but show as positive for display)
        double maxDrawdown = Math.abs(trades.stream()
                .mapToDouble(SimulatedTradeRecord::realizedPnL)
                .filter(pnl -> pnl < 0)
                .min()
                .orElse(0.0));

        int winningTrades = (int) trades.stream()
                .filter(t -> t.realizedPnL() > 0)
                .count();
        int losingTrades = (int) trades.stream()
                .filter(t -> t.realizedPnL() < 0)
                .count();

        // Calculate win rate
        double winRate = trades.isEmpty() ? 0 : (double) winningTrades / trades.size() * 100;

        // Calculate total return rate
        double totalReturnRate = initialBalance > 0 ? (totalProfit / initialBalance) * 100 : 0;

        // Find trading span
        LocalDateTime earliest = trades.stream()
                .map(SimulatedTradeRecord::entryTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latest = trades.stream()
                .map(SimulatedTradeRecord::entryTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new PortfolioStatisticsOutputData(
                totalProfit, maxGain, maxDrawdown, trades.size(),
                winningTrades, losingTrades, winRate, totalReturnRate, earliest, latest
        );
    }
}
