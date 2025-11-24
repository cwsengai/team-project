package interface_adapter.setup_simulation;

import use_case.setup_simulation.SetupInputBoundary;
import use_case.setup_simulation.SetupInputData;

public class SetupController {
    private final SetupInputBoundary setupInteractor;

    public SetupController(SetupInputBoundary setupInteractor) {
        this.setupInteractor = setupInteractor;
    }

    public void execute(String ticker, double initialBalance, int speedMultiplier) {
        SetupInputData inputData = new SetupInputData(ticker, initialBalance, speedMultiplier);
        setupInteractor.execute(inputData);
    }
}