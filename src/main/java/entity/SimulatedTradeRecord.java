package entity;

import java.time.LocalDateTime;

public class SimulatedTradeRecord {

    private final String ticker;
    private final boolean isLong;
    private final int quantity;
    private final double entryPrice;
    private final double exitPrice;
    private final double realizedPnL;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final String userId;

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
     * Calculates the return rate (ROI) for this trade as a percentage.
     *
     * <p>
     * For long positions, the formula used is:<br>
     * <code>((exitPrice - entryPrice) / entryPrice) * 100</code><br>
     * For short positions, the formula used is:<br>
     * <code>((entryPrice - exitPrice) / entryPrice) * 100</code>
     * </p>
     *
     * @return the return rate as a percentage; returns {@code 0.0} if the entry
     *         price is zero to avoid division by zero
     */
    public double getReturnRate() {
        if (entryPrice == 0) {
            // Avoid division by zero
            return 0.0;
        }
        if (isLong) {
            return ((exitPrice - entryPrice) / entryPrice) * 100;
        }
        else {
            return ((entryPrice - exitPrice) / entryPrice) * 100;
        }
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

// SimulatedTradingRecord rec = new SimulatedTradingRecord(
//        "TSLA",
//        true,            // true = buy/long    false = sell/short
//        5,               // quantity
//        245.30,          // entry price
//        251.80,          // close price
//        32.50,           // total profit
//        LocalDateTime.of(2025, 1, 1, 10, 5), //entry time
//        LocalDateTime.of(2025, 1, 1, 10, 21)  // exit time
//        );
