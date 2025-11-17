package entity;
import java.time.LocalDateTime;

public class SimulatedOrder {
    private final String orderId;
    private final String ticker;   //Stock code
    private final boolean isLong;   //（true = buy，false = short）
    private final int quantity;
    private final double limitPrice;   //Limit order price
    private LocalDateTime entryTime;   //entry time
    private double entryPrice;
    private boolean isFilled;

    public SimulatedOrder(String orderId, String ticker, boolean isLong, int quantity,
                          double limitPrice,  LocalDateTime entryTime) {
        this.orderId = orderId;
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.entryTime = entryTime;
        this.entryPrice = 0.0;  // no price before fill
        this.isFilled = false;
    }

    public String getOrderId() {
        return orderId;
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

    public double getLimitPrice() {
        return limitPrice;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        this.isFilled = filled;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }
}
