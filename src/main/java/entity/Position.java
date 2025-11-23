package entity;

public class Position {
    private final String ticker;
    private boolean isLong;       // true = Long, false = Short
    private int quantity;         // Absolute quantity (always >= 0)
    private double avgPrice;      // Weighted average cost price

    public Position(String ticker, boolean isLong, int quantity, double avgPrice) {
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    /**
     * Updates position based on trade action.
     * @return Realized PnL (only returns value when reducing or closing position).
     */
    public double update(boolean isBuyAction, int tradeQty, double tradePrice) {
        // Determine if adding to position (Same direction) or reducing/flipping (Opposite direction)
        boolean isIncrease = (isLong && isBuyAction) || (!isLong && !isBuyAction);

        if (quantity == 0) {
            this.isLong = isBuyAction;
            this.quantity = tradeQty;
            this.avgPrice = tradePrice;
            return 0.0;
        }

        if (isIncrease) {
            // Calculate new Weighted Average Price
            double totalCost = (avgPrice * quantity) + (tradePrice * tradeQty);
            quantity += tradeQty;
            avgPrice = totalCost / quantity;
            return 0.0;
        } else {
            double pnl = 0.0;
            // PnL logic: (Sell - Cost) for Long; (Cost - Sell) for Short
            double priceDiff = isLong ? (tradePrice - avgPrice) : (avgPrice - tradePrice);

            if (tradeQty <= quantity) {
                // Partial or full close
                pnl = priceDiff * tradeQty;
                quantity -= tradeQty;
            } else {
                // Position Flip: Close current -> Open reverse
                pnl = priceDiff * quantity; // Realize PnL on existing quantity

                // Open new position with remaining qty
                this.quantity = tradeQty - quantity;
                this.isLong = !this.isLong;
                this.avgPrice = tradePrice;
            }
            return pnl;
        }
    }

    public double getUnrealizedPnL(double currentPrice) {
        if (quantity == 0) return 0.0;
        // Short position profits when price drops (Avg > Current)
        double diff = isLong ? (currentPrice - avgPrice) : (avgPrice - currentPrice);
        return diff * quantity;
    }

    public String getTicker() { return ticker; }
    public boolean isLong() { return isLong; }
    public int getQuantity() { return quantity; }
    public double getAvgPrice() { return avgPrice; }
}