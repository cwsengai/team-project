package usecase.simulated_trade;

/**
 * Data transfer object representing the output of a simulated trade.
 * Contains the updated account balance and a status message.
 */
public record SimulatedTradeOutputData(double newBalance, String message) {

    /**
     * Constructs an object representing the result of a simulated trade.
     *
     * @param newBalance the updated account balance after the trade
     * @param message    the status or information message to display
     */
    public SimulatedTradeOutputData {
    }

    /**
     * Returns the updated account balance.
     *
     * @return the new balance
     */
    @Override
    public double newBalance() {
        return newBalance;
    }

    /**
     * Returns the status or information message.
     *
     * @return the message string
     */
    @Override
    public String message() {
        return message;
    }
}
