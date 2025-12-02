package interfaceadapter.setup_simulation;

import usecase.setup_simulation.SetupInputBoundary;
import usecase.setup_simulation.SetupInputData;

/**
 * Controller for handling the setup of a new simulation session.
 * It receives the raw input from the UI layer, packages it into
 * input data, and passes it to the setup interactor.
 *
 * @param setupInteractor The interactor responsible for executing the setup use case.
 */
public record SetupController(SetupInputBoundary setupInteractor) {

    /**
     * Creates a new {@code SetupController}.
     *
     * @param setupInteractor the interactor that performs the setup logic
     */
    public SetupController {
    }

    /**
     * Executes the setup simulation use case with the provided parameters.
     *
     * @param ticker          the stock ticker symbol for the simulation
     * @param initialBalance  the initial balance for the trading account
     * @param speedMultiplier the speed multiplier for the simulation
     */
    public void execute(String ticker, double initialBalance, int speedMultiplier) {
        final SetupInputData inputData =
                new SetupInputData(ticker, initialBalance, speedMultiplier);
        setupInteractor.execute(inputData);
    }
}
