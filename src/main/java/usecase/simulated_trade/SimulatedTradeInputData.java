package usecase.simulated_trade;

/**
 * Input data required to execute a simulated trade.
 * Contains trade direction, amount, and the current market price.
 */
public record SimulatedTradeInputData(String ticker, boolean isBuyAction, double amount, double currentPrice) {

    /**
     * Creates an object representing the input data for a simulated trade.
     *
     * @param ticker       the stock ticker symbol
     * @param isBuyAction  true if the trade is a buy order, false if sell
     * @param amount       the number of units involved in the trade
     * @param currentPrice the current market price of the asset
     */
    public SimulatedTradeInputData {
    }

    /**
     * Returns the trade ticker symbol.
     *
     * @return the ticker
     */
    @Override
    public String ticker() {
        return ticker;
    }

    /**
     * Indicates whether the trade is a buy action.
     *
     * @return true if buy, false if sell
     */
    @Override
    public boolean isBuyAction() {
        return isBuyAction;
    }

    /**
     * Returns the trade amount.
     *
     * @return the amount
     */
    @Override
    public double amount() {
        return amount;
    }

    /**
     * Returns the current market price.
     *
     * @return the price
     */
    @Override
    public double currentPrice() {
        return currentPrice;
    }
}
