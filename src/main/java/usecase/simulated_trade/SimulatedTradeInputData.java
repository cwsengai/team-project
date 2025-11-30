package usecase.simulated_trade;

/**
 * Input data required to execute a simulated trade.
 * Contains trade direction, amount, and the current market price.
 */
public class SimulatedTradeInputData {

    private final String ticker;
    private final boolean isBuyAction;
    private final double amount;
    private final double currentPrice;

    /**
     * Creates an object representing the input data for a simulated trade.
     *
     * @param ticker the stock ticker symbol
     * @param isBuyAction true if the trade is a buy order, false if sell
     * @param amount the number of units involved in the trade
     * @param currentPrice the current market price of the asset
     */
    public SimulatedTradeInputData(String ticker,
                                   boolean isBuyAction,
                                   double amount,
                                   double currentPrice) {
        this.ticker = ticker;
        this.isBuyAction = isBuyAction;
        this.amount = amount;
        this.currentPrice = currentPrice;
    }

    /**
     * Returns the trade ticker symbol.
     *
     * @return the ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Indicates whether the trade is a buy action.
     *
     * @return true if buy, false if sell
     */
    public boolean isBuyAction() {
        return isBuyAction;
    }

    /**
     * Returns the trade amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the current market price.
     *
     * @return the price
     */
    public double getCurrentPrice() {
        return currentPrice;
    }
}
