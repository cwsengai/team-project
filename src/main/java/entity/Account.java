package entity;

import use_case.simulated_trade.TradeClosedListener; // Essential for notifying external components
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Account {
    // Core Funds
    private double balance;
    private final double initialBalance;
    private double maxEquity;

    // Holdings and Listeners
    private final Map<String, Position> positions = new HashMap<>();
    private final List<TradeClosedListener> listeners = new ArrayList<>(); // ✅ New: Event Listeners

    // Performance Stats (Counters for Figma Summary)
    private int totalTrades = 0;
    private int winningTrades = 0;
    private double maxGain = 0.0;

    public Account(double initialBalance) {
        this.initialBalance = initialBalance;
        this.balance = initialBalance;
        this.maxEquity = initialBalance;
    }

    // --- Observer Pattern Methods ---
    public void addTradeClosedListener(TradeClosedListener listener) {
        this.listeners.add(listener);
    }
    // --------------------------------

    // Executes a trade logic: updates cash, updates position, updates stats.
    public void executeTrade(String ticker, boolean isBuyAction, int quantity, double price, LocalDateTime time) {
        double transactionValue = quantity * price;
        if (isBuyAction) {
            balance -= transactionValue;
        } else {
            balance += transactionValue;
        }

        // Save current state before update for history record
        double entryPriceBefore = positions.containsKey(ticker) ? positions.get(ticker).getAvgPrice() : 0.0;
        boolean wasLong = positions.containsKey(ticker) && positions.get(ticker).isLong();


        positions.putIfAbsent(ticker, new Position(ticker, isBuyAction, 0, 0));
        Position position = positions.get(ticker);
        double realizedPnL = position.update(isBuyAction, quantity, price);

        if (realizedPnL != 0) {
            totalTrades++;

            if (realizedPnL > 0) {
                winningTrades++;
            }
            if (realizedPnL > maxGain) {
                maxGain = realizedPnL;
            }

            SimulatedTradeRecord record = new SimulatedTradeRecord(
                    ticker,
                    wasLong, // Direction of the position being closed
                    quantity, // Quantity closed/affected
                    entryPriceBefore, // Average Entry Price
                    price, // Exit Price
                    realizedPnL,
                    time, // Simplified entry time
                    time // Exit time (current time)
            );

            for (TradeClosedListener listener : listeners) {
                listener.onTradeClosed(record);
            }
        }

        if (position.getQuantity() == 0) {
            positions.remove(ticker);
        }
    }

    // Calculates the real-time Total Equity (Net Worth).
    public double calculateTotalEquity(double currentPrice, String currentTicker) {
        double totalUnrealizedPnL = 0.0;

        for (Position pos : positions.values()) {
            double priceToUse = pos.getTicker().equals(currentTicker) ? currentPrice : pos.getAvgPrice();

            totalUnrealizedPnL += pos.getUnrealizedPnL(priceToUse);
        }

        double currentEquity = balance + totalUnrealizedPnL;
        if (currentEquity > maxEquity) {
            maxEquity = currentEquity;
        }

        return currentEquity;
    }

    // UI: "available virtual money"
    public double getBalance() { return balance; }

    // UI: "Total Profit"
    public double getTotalProfit(double currentEquity) { return currentEquity - initialBalance; }

    // UI: "Total Return Rate"
    public double getTotalReturnRate(double currentEquity) {
        if (initialBalance == 0) return 0.0;
        return (currentEquity - initialBalance) / initialBalance;
    }

    // UI: "Max Drawdown"
    public double getMaxDrawdown(double currentEquity) { return Math.max(0.0, maxEquity - currentEquity); }

    // UI: "Max Gain"
    public double getMaxGain() { return maxGain; }

    // UI: "Total Trades#"
    public int getTotalTrades() { return totalTrades; }

    // UI: "Winning Trades#"
    public int getWinningTrades() { return winningTrades; }

    // UI: "Win Rate"
    public double getWinRate() {
        if (totalTrades == 0) return 0.0;
        return (double) winningTrades / totalTrades;
    }

    // UI: "Losing Trades"
    public int getLosingTrades() { return totalTrades - winningTrades; }

    // UI: "Wallet"
    public Map<String, Position> getPositions() { return positions; }
}