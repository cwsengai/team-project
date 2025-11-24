package use_case.setup_simulation;

import use_case.simulated_trade.SimulationDataAccessInterface;

public class SetupInteractor implements SetupInputBoundary {

    private final SetupOutputBoundary presenter;
    private final SimulationDataAccessInterface dataAccess;

    public SetupInteractor(SetupOutputBoundary presenter, SimulationDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(SetupInputData input) {
        if (input.getSpeedMultiplier() < 5 || input.getSpeedMultiplier() > 30 || input.getSpeedMultiplier() % 5 != 0) {
            presenter.prepareFailView("Invalid speed. Must be 5x, 10x, 20x, or 30x.");
            return;
        }
        if (input.getInitialBalance() <= 0) {
            presenter.prepareFailView("Initial balance must be positive.");
            return;
        }
        presenter.prepareSuccessView(input);
    }
}