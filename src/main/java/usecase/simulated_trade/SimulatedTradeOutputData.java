package usecase.simulated_trade;

/**
 * Data transfer object representing the output of a simulated trade.
 * Contains the updated account balance and a status message.
 */
public class SimulatedTradeOutputData {

    private final double newBalance;
    private final String message;

    /**
     * Constructs an object representing the result of a simulated trade.
     *
     * @param newBalance the updated account balance after the trade
     * @param message the status or information message to display
     */
    public SimulatedTradeOutputData(double newBalance, String message) {
        this.newBalance = newBalance;
        this.message = message;
    }

    /**
     * Returns the updated account balance.
     *
     * @return the new balance
     */
    public double getNewBalance() {
        return newBalance;
    }

    /**
     * Returns the status or information message.
     *
     * @return the message string
     */
    public String getMessage() {
        return message;
    }
}
