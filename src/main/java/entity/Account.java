package entity;

import java.util.HashMap;
import java.util.Map;

public class Account {
    // Core Funds
    private double balance;
    private final double initialBalance;
    private double maxEquity;

    // Holdings
    private final Map<String, Position> positions = new HashMap<>();

    // Performance Stats (Counters for Figma Summary)
    private int totalTrades = 0;
    private int winningTrades = 0;
    private double maxGain = 0.0;

    public Account(double initialBalance) {
        this.initialBalance = initialBalance;
        this.balance = initialBalance;
        // Initially, the max equity is just the starting balance.
        this.maxEquity = initialBalance;
    }

    //Executes a trade logic: updates cash, updates position, updates stats.
    public void executeTrade(String ticker, boolean isBuyAction, int quantity, double price) {
        double transactionValue = quantity * price;
        if (isBuyAction) {
            balance -= transactionValue;
        } else {
            balance += transactionValue;
        }
        positions.putIfAbsent(ticker, new Position(ticker, isBuyAction, 0, 0));
        Position position = positions.get(ticker);
        double realizedPnL = position.update(isBuyAction, quantity, price);
        if (realizedPnL != 0) {
            totalTrades++;

            if (realizedPnL > 0) {
                winningTrades++;
            }
            // Check for Max Gain
            if (realizedPnL > maxGain) {
                maxGain = realizedPnL;
            }
        }
        if (position.getQuantity() == 0) {
            positions.remove(ticker);
        }
    }

    //Calculates the real-time Total Equity (Net Worth).
    public double calculateTotalEquity(double currentPrice, String currentTicker) {
        double totalUnrealizedPnL = 0.0;

        for (Position pos : positions.values()) {
            double priceToUse = pos.getTicker().equals(currentTicker) ? currentPrice : pos.getAvgPrice();

            totalUnrealizedPnL += pos.getUnrealizedPnL(priceToUse);
        }

        double currentEquity = balance + totalUnrealizedPnL;
        // Track historical peak for Max Drawdown calculation
        if (currentEquity > maxEquity) {
            maxEquity = currentEquity;
        }

        return currentEquity;
    }

    // UI: "available virtual money"
    public double getBalance() {
        return balance;
    }

    // UI: "Total Profit"
    public double getTotalProfit(double currentEquity) {
        return currentEquity - initialBalance;
    }


    // UI: "Total Return Rate"
    public double getTotalReturnRate(double currentEquity) {
        if (initialBalance == 0) return 0.0;
        return (currentEquity - initialBalance) / initialBalance;
    }

    public int getWinningTrades() {
        return winningTrades;
    }

    // UI: "Max Drawdown"
    public double getMaxDrawdown(double currentEquity) {
        return Math.max(0.0, maxEquity - currentEquity);
    }

    // UI: "Max Gain"
    public double getMaxGain() {
        return maxGain;
    }

    // UI: "Total Trades#"
    public int getTotalTrades() {
        return totalTrades;
    }

    // UI: "Win Rate"
    public double getWinRate() {
        if (totalTrades == 0) return 0.0;
        return (double) winningTrades / totalTrades;
    }

    public int getLosingTrades() {
        return totalTrades - winningTrades;
    }

    // UI: "Wallet"
    public Map<String, Position> getPositions() {
        return positions;
    }
}