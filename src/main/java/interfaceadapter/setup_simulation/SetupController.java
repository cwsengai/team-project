package interfaceadapter.setup_simulation;

import usecase.setup_simulation.SetupInputBoundary;
import usecase.setup_simulation.SetupInputData;

/**
 * Controller for handling the setup of a new simulation session.
 * It receives the raw input from the UI layer, packages it into
 * input data, and passes it to the setup interactor.
 */
public class SetupController {

    /** The interactor responsible for executing the setup use case. */
    private final SetupInputBoundary setupInteractor;

    /**
     * Creates a new {@code SetupController}.
     *
     * @param setupInteractor the interactor that performs the setup logic
     */
    public SetupController(SetupInputBoundary setupInteractor) {
        this.setupInteractor = setupInteractor;
    }

    /**
     * Executes the setup use case by creating a {@code SetupInputData} object
     * and passing it to the interactor.
     *
     * @param ticker the stock ticker symbol to simulate
     * @param initialBalance the starting balance for the simulation
     * @param speedMultiplier the speed at which the simulation should run
     */
    public void execute(String ticker, double initialBalance, int speedMultiplier) {
        final SetupInputData inputData =
                new SetupInputData(ticker, initialBalance, speedMultiplier);
        setupInteractor.execute(inputData);
    }
}
