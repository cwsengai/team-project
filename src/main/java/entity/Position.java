package entity;

/**
 * Represents a trading position with direction, quantity, and average entry price.
 */
public class Position {

    private final String ticker;
    private boolean isLong;
    private int quantity;
    private double avgPrice;

    /**
     * Constructs a Position.
     *
     * @param ticker   stock ticker
     * @param isLong   true for long, false for short
     * @param quantity initial quantity
     * @param avgPrice initial average price
     */
    public Position(String ticker, boolean isLong, int quantity, double avgPrice) {
        this.ticker = ticker;
        this.isLong = isLong;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    /**
     * Updates the position with a new trade.
     *
     * @param isBuyAction true if buy, false if sell
     * @param tradeQty    traded quantity
     * @param tradePrice  traded price
     * @return realized PnL if closing/reducing, 0 otherwise
     */
    public double update(boolean isBuyAction, int tradeQty, double tradePrice) {

        double realizedPnl = 0.0;

        final boolean isIncrease =
                isLong && isBuyAction || !isLong && !isBuyAction;

        if (quantity == 0) {
            isLong = isBuyAction;
            quantity = tradeQty;
            avgPrice = tradePrice;

        }
        else {

            if (isIncrease) {
                final double totalCost = avgPrice * quantity + tradePrice * tradeQty;
                quantity = quantity + tradeQty;
                avgPrice = totalCost / quantity;

            }
            else {

                final double priceDifference;
                if (isLong) {
                    priceDifference = tradePrice - avgPrice;
                }
                else {
                    priceDifference = avgPrice - tradePrice;
                }

                if (tradeQty <= quantity) {
                    realizedPnl = priceDifference * tradeQty;
                    quantity = quantity - tradeQty;
                }
                else {
                    realizedPnl = priceDifference * quantity;

                    final int remaining = tradeQty - quantity;
                    quantity = remaining;
                    isLong = !isLong;
                    avgPrice = tradePrice;
                }
            }
        }

        return realizedPnl;
    }

    /**
     * Computes unrealized PnL of this position.
     *
     * @param currentPrice current market price
     * @return unrealized PnL
     */

    public double getUnrealizedPnL(double currentPrice) {

        double result;

        if (quantity == 0) {
            result = 0.0;
        }
        else {
            final double difference;
            if (isLong) {
                difference = currentPrice - avgPrice;
            }
            else {
                difference = avgPrice - currentPrice;
            }
            result = difference * quantity;
        }

        return result;
    }
    /**
     * Returns ticker symbol.
     *
     * @return ticker
     */

    public String getTicker() {
        return ticker;
    }

    /**
     * Returns whether the position is long.
     *
     * @return true if long
     */
    public boolean isLong() {
        return isLong;
    }

    /**
     * Returns quantity.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns average entry price.
     *
     * @return average price
     */
    public double getAvgPrice() {
        return avgPrice;
    }
}
