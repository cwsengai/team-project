package interfaceadapter.setup_simulation;

import usecase.setup_simulation.SetupInputBoundary;
import usecase.setup_simulation.SetupInputData;

public class SetupController {
    private final SetupInputBoundary setupInteractor;

    public SetupController(SetupInputBoundary setupInteractor) {
        this.setupInteractor = setupInteractor;
    }

    /**
     * Executes the setup simulation use case with the provided parameters.
     *
     * @param ticker the stock ticker symbol for the simulation
     * @param initialBalance the initial balance for the trading account
     * @param speedMultiplier the speed multiplier for the simulation
     */
    public void execute(String ticker, double initialBalance, int speedMultiplier) {
        SetupInputData inputData = new SetupInputData(ticker, initialBalance, speedMultiplier);
        setupInteractor.execute(inputData);
    }
}
