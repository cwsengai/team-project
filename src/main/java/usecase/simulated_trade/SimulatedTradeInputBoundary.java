package usecase.simulated_trade;

/**
 * Input boundary for executing simulated trades in the trading use case.
 * Defines the method required to initiate a simulated trade operation.
 */
public interface SimulatedTradeInputBoundary {

    /**
     * Executes a simulated trade using the provided input data.
     *
     * @param inputData the data describing the trade to execute
     */
    void executeTrade(SimulatedTradeInputData inputData);
}
