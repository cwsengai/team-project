package usecase.update_market;

/**
 * Output boundary for the UpdateMarket use case.
 *
 * <p>This interface defines how the interactor communicates results
 * to the presenter, including both success and failure views.</p>
 */
public interface UpdateMarketOutputBoundary {

    /**
     * Returns a success view containing updated market data.
     *
     * @param outputData the updated market information
     */
    void prepareSuccessView(UpdateMarketOutputData outputData);

    /**
     * Returns a failure view with an error message.
     *
     * @param error the error message to present
     */
    void prepareFailView(String error);
}
