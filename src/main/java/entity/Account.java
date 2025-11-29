package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import use_case.simulated_trade.TradeClosedListener;

/**
 * Represents a trading account tracking balance, positions,
 * trade statistics, and equity performance.
 */
public class Account {

    private static final double ZERO = 0.0;

    /** User ID for tracking owner of the account. */
    private final String userId;

    /** Current available balance. */
    private double balance;

    /** Initial starting balance. */
    private final double initialBalance;

    /** Highest historical equity value. */
    private double maxEquity;

    /** Open positions keyed by ticker symbol. */
    private final Map<String, Position> positions = new HashMap<>();

    /** Listeners to be notified when trades close. */
    private final List<TradeClosedListener> listeners = new ArrayList<>();

    /** Total number of closed trades. */
    private int totalTrades;

    /** Total number of winning trades. */
    private int winningTrades;

    /** Largest single realized gain. */
    private double maxGain = ZERO;

    /**
     * Constructs an Account.
     *
     * @param initialBalance starting balance
     * @param userId user identifier
     */
    public Account(double initialBalance, String userId) {
        this.initialBalance = initialBalance;
        this.balance = initialBalance;
        this.maxEquity = initialBalance;
        this.userId = userId;
    }

    /**
     * Adds a listener that will be notified when a trade closes.
     *
     * @param listener trade close listener
     */
    public void addTradeClosedListener(TradeClosedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Executes a trade and updates account balance, positions, and stats.
     *
     * @param ticker ticker symbol
     * @param isBuyAction true if buy, false if sell
     * @param quantity number of shares
     * @param price trade price
     * @param time trade time
     */
    public void executeTrade(String ticker, boolean isBuyAction, int quantity,
                             double price, LocalDateTime time) {

        final double transactionValue = quantity * price;

        if (isBuyAction) {
            balance -= transactionValue;
        }
        else {
            balance += transactionValue;
        }

        double entryPriceBefore = ZERO;
        boolean wasLong = false;

        if (positions.containsKey(ticker)) {
            final Position existing = positions.get(ticker);
            entryPriceBefore = existing.getAvgPrice();
            wasLong = existing.isLong();
        }

        positions.putIfAbsent(ticker, new Position(ticker, isBuyAction, 0, 0));
        final Position position = positions.get(ticker);

        final double realizedPnL = position.update(isBuyAction, quantity, price);

        if (realizedPnL != ZERO) {
            totalTrades++;

            if (realizedPnL > ZERO) {
                winningTrades++;
            }

            if (realizedPnL > maxGain) {
                maxGain = realizedPnL;
            }

            final SimulatedTradeRecord record = new SimulatedTradeRecord(
                    ticker,
                    wasLong,
                    quantity,
                    entryPriceBefore,
                    price,
                    realizedPnL,
                    time,
                    time,
                    this.userId
            );

            for (TradeClosedListener listener : listeners) {
                listener.onTradeClosed(record);
            }
        }

        if (position.getQuantity() == 0) {
            positions.remove(ticker);
        }
    }

    /**
     * Calculates current total equity.
     *
     * @param currentPrice price of the currently updated ticker
     * @param currentTicker ticker being updated
     * @return total equity
     */
    public double calculateTotalEquity(double currentPrice, String currentTicker) {
        double totalUnrealizedPnL = ZERO;
        double totalCostBasis = ZERO;

        for (Position pos : positions.values()) {
            final boolean isCurrent = pos.getTicker().equals(currentTicker);
            final double referencePrice;
            if (isCurrent) {
                referencePrice = currentPrice;
            }
            else {
                referencePrice = pos.getAvgPrice();
            }

            totalUnrealizedPnL += pos.getUnrealizedPnL(referencePrice);
            totalCostBasis += pos.getQuantity() * pos.getAvgPrice();
        }

        final double currentEquity = balance + totalCostBasis + totalUnrealizedPnL;

        if (currentEquity > maxEquity) {
            maxEquity = currentEquity;
        }

        return currentEquity;
    }

    /**
     * Returns available balance.
     *
     * @return balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Returns total profit.
     *
     * @param currentEquity current total equity
     * @return profit
     */
    public double getTotalProfit(double currentEquity) {
        return currentEquity - initialBalance;
    }

    /**
     * Returns total return rate.
     *
     * @param currentEquity current total equity
     * @return return percentage
     */
    public double getTotalReturnRate(double currentEquity) {
        final double rate;
        if (initialBalance == ZERO) {
            rate = ZERO;
        }
        else {
            rate = (currentEquity - initialBalance) / initialBalance;
        }
        return rate;
    }

    /**
     * Returns maximum drawdown.
     *
     * @param currentEquity current equity
     * @return max drawdown
     */
    public double getMaxDrawdown(double currentEquity) {
        final double diff = maxEquity - currentEquity;
        return Math.max(ZERO, diff);
    }

    /**
     * Returns maximum single realized gain.
     *
     * @return max gain
     */
    public double getMaxGain() {
        return maxGain;
    }

    /**
     * Returns number of completed trades.
     *
     * @return count of trades
     */
    public int getTotalTrades() {
        return totalTrades;
    }

    /**
     * Returns number of winning trades.
     *
     * @return winning trades count
     */
    public int getWinningTrades() {
        return winningTrades;
    }

    /**
     * Returns win rate.
     *
     * @return win rate between 0–1
     */
    public double getWinRate() {
        final double rate;
        if (totalTrades == 0) {
            rate = ZERO;
        }
        else {
            rate = (double) winningTrades / totalTrades;
        }
        return rate;
    }

    /**
     * Returns number of losing trades.
     *
     * @return losing trades count
     */
    public int getLosingTrades() {
        final int result = totalTrades - winningTrades;
        return result;
    }

    /**
     * Returns open positions.
     *
     * @return position map
     */
    public Map<String, Position> getPositions() {
        return positions;
    }
}
