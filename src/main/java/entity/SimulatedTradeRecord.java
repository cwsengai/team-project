package entity;

import java.time.LocalDateTime;

/**
 * Represents a completed simulated trade, including entry/exit data and
 * calculated PnL metrics. Immutable record of a user's executed trade.
 */
public record SimulatedTradeRecord(String ticker, boolean isLong, int quantity, double entryPrice, double exitPrice,
                                   double realizedPnL, LocalDateTime entryTime, LocalDateTime exitTime, String userId) {

    /**
     * Constant for representing zero to avoid magic numbers.
     */
    private static final double ZERO = 0.0;

    /**
     * Constant representing 100.0 for percentage calculations.
     */
    private static final double HUNDRED = 100.0;

    /**
     * Creates a new immutable simulated trade record.
     *
     * @param ticker      the stock ticker
     * @param isLong      true if long, false if short
     * @param quantity    number of shares
     * @param entryPrice  trade entry price
     * @param exitPrice   trade exit price
     * @param realizedPnL realized profit/loss of the trade
     * @param entryTime   time the trade was opened
     * @param exitTime    time the trade was closed
     * @param userId      the ID of the user who executed the trade
     */
    public SimulatedTradeRecord {
    }

    /**
     * Calculates the percentage return rate (ROI) for this trade.
     *
     * @return the return rate as a percentage; returns zero if entry price is zero
     */
    public double getReturnRate() {
        double result = ZERO;

        if (entryPrice != ZERO) {
            double priceDiff;

            if (isLong) {
                priceDiff = exitPrice - entryPrice;
            } else {
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
