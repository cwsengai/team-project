package entity;

public class SimulatedOrder {
    private final String orderId;
    private double entryPrice;
    private boolean isFilled;

    public SimulatedOrder(String orderId) {
        this.orderId = orderId;
        this.entryPrice = 0.0;
        this.isFilled = false;
    }

    public String getOrderId() {
        return orderId;
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
