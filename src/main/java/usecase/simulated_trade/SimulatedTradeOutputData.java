package usecase.simulated_trade;

public class SimulatedTradeOutputData {
    private final double newBalance;
    private final String message; // Success message to display

    public SimulatedTradeOutputData(double newBalance, String message) {
        this.newBalance = newBalance;
        this.message = message;
    }

    public double getNewBalance() { return newBalance; }
    public String getMessage() { return message; }
}