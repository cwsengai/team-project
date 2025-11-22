package entity;

public class Position {
    //position :-current direction  -current quantity -current average cost price
    private final String ticker;  // stock code
    private boolean isLong;       // current durection
    private int quantity;         // current position quantity
    private double avgPrice;      // current averagecost price

    public Position(String ticker, boolean isLong, int quantity, double avgPrice) {
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
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

    public double getAvgPrice() {
        return avgPrice;
    }

    //Increase position (in the same direction) Recalculate the average cost price
    public void add(int qty, double fillPrice) {
        double totalCost = avgPrice * quantity + fillPrice * qty;
        quantity += qty;
        avgPrice = totalCost / quantity;
    }

    // Reduce position (close position/partially close position) ,Do not change the avgPrice
    public void reduce(int qty) {
        quantity -= qty;
        if (quantity < 0) {
            quantity = 0;
        }
    }

    // After liquidating the position, open a reverse position (flip the position)
    public void flip(boolean newIsLong, int newQty, double newAvgPrice) {
        this.isLong = newIsLong;
        this.quantity = newQty;
        this.avgPrice = newAvgPrice;
    }
}
