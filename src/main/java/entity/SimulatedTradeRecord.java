package entity;

import java.time.LocalDateTime;

/**
 * Represents a completed simulated trade, including entry/exit data and
 * calculated PnL metrics. Immutable record of a user's executed trade.
 */
public class SimulatedTradeRecord {

    /** Constant for representing zero to avoid magic numbers. */
    private static final double ZERO = 0.0;

    /** Constant representing 100.0 for percentage calculations. */
    private static final double HUNDRED = 100.0;

    private final String ticker;
    private final boolean isLong;
    private final int quantity;
    private final double entryPrice;
    private final double exitPrice;
    private final double realizedPnL;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final String userId;

    /**
     * Creates a new immutable simulated trade record.
     *
     * @param ticker the stock ticker
     * @param isLong true if long, false if short
     * @param quantity number of shares
     * @param entryPrice trade entry price
     * @param exitPrice trade exit price
     * @param realizedPnL realized profit/loss of the trade
     * @param entryTime time the trade was opened
     * @param exitTime time the trade was closed
     * @param userId the ID of the user who executed the trade
     */
    public SimulatedTradeRecord(String ticker,
                                boolean isLong,
                                int quantity,
                                double entryPrice,
                                double exitPrice,
                                double realizedPnL,
                                LocalDateTime entryTime,
                                LocalDateTime exitTime,
                                String userId) {
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.realizedPnL = realizedPnL;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.userId = userId;
    }

    public String getTicker() {
        return ticker;
    }

    public boolean isLong() {
        return isLong;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public double getRealizedPnL() {
        return realizedPnL;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Calculates the percentage return rate (ROI) for this trade.
     *
     * @return the return rate as a percentage; returns zero if entry price is zero
     */
    public double getReturnRate() {
        double result = ZERO;

        if (entryPrice != ZERO) {
            double priceDiff = ZERO;

            if (isLong) {
                priceDiff = exitPrice - entryPrice;
            }
            else {
                priceDiff = entryPrice - exitPrice;
            }

            result = (priceDiff / entryPrice) * HUNDRED;
        }

        return result;
    }

    @Override
    public String toString() {
        return "SimulatedTradingRecord{"
                + "ticker='" + ticker + '\''
                + ", isLong=" + isLong
                + ", quantity=" + quantity
                + ", entryPrice=" + entryPrice
                + ", exitPrice=" + exitPrice
                + ", realizedPnL=" + realizedPnL
                + ", entryTime=" + entryTime
                + ", exitTime=" + exitTime
                + '}';
    }
}
