package usecase.simulated_trade;

/**
 * Output boundary for presenting the result of a simulated trade.
 */
public interface SimulatedTradeOutputBoundary {

    /**
     * Displays the success result of a completed trade.
     *
     * @param outputData the output data containing balance and message
     */
    void prepareSuccessView(SimulatedTradeOutputData outputData);

    /**
     * Displays an error message when a trade fails.
     *
     * @param error the error message
     */
    void prepareFailView(String error);
}
