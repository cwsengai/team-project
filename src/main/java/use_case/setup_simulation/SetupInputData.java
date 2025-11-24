package use_case.setup_simulation;

public class SetupInputData {
    private final String ticker;
    private final double initialBalance;
    private final int speedMultiplier;

    public SetupInputData(String ticker, double initialBalance, int speedMultiplier) {
        this.ticker = ticker;
        this.initialBalance = initialBalance;
        this.speedMultiplier = speedMultiplier;
    }

    public String getTicker() { return ticker; }
    public double getInitialBalance() { return initialBalance; }
    public int getSpeedMultiplier() { return speedMultiplier; }
}