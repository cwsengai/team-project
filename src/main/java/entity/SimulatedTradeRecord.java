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

    public SimulatedTradeRecord(String ticker,
                                boolean isLong,
                                int quantity,
                                double entryPrice,
                                double exitPrice,
                                double realizedPnL,
                                LocalDateTime entryTime,
                                LocalDateTime exitTime) {
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.realizedPnL = realizedPnL;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
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

    @Override
    public String toString() {
        return "SimulatedTradingRecord{" +
                "ticker='" + ticker + '\'' +
                ", isLong=" + isLong +
                ", quantity=" + quantity +
                ", entryPrice=" + entryPrice +
                ", exitPrice=" + exitPrice +
                ", realizedPnL=" + realizedPnL +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                '}';
    }
}



//SimulatedTradingRecord rec = new SimulatedTradingRecord(
//        "TSLA",
//        true,            // true = buy/long    false = sell/short
//        5,               // quantity
//        245.30,          // entry price
//        251.80,          // close price
//        32.50,           // total profit
//        LocalDateTime.of(2025, 1, 1, 10, 5), //entry time
//        LocalDateTime.of(2025, 1, 1, 10, 21)  // exit time
//        );
