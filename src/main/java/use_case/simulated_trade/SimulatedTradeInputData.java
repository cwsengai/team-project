package use_case.simulated_trade;

public class SimulatedTradeInputData {
    private final String ticker;
    private final boolean isBuyAction; // true = Buy/Long, false = Sell/Short
    private final double amount;       // Input amount in USD
    private final double currentPrice; // Market price at the moment of trade

    public SimulatedTradeInputData(String ticker, boolean isBuyAction, double amount, double currentPrice) {
        this.ticker = ticker;
        this.isBuyAction = isBuyAction;
        this.amount = amount;
        this.currentPrice = currentPrice;
    }

    public String getTicker() { return ticker; }
    public boolean isBuyAction() { return isBuyAction; }
    public double getAmount() { return amount; }
    public double getCurrentPrice() { return currentPrice; }
}