package usecase.setup_simulation;

import usecase.simulated_trade.SimulationDataAccessInterface;

/**
 * Interactor for handling the setup simulation use case.
 */
public class SetupInteractor implements SetupInputBoundary {

    private static final int MIN_SPEED = 5;
    private static final int MAX_SPEED = 30;
    private static final int SPEED_STEP = 5;

    private final SetupOutputBoundary presenter;

    public SetupInteractor(SetupOutputBoundary presenter, SimulationDataAccessInterface dataAccess) {
        this.presenter = presenter;
    }

    @Override
    public void execute(SetupInputData input) {

        final boolean hasInvalidSpeed =
                input.getSpeedMultiplier() < MIN_SPEED
                        || input.getSpeedMultiplier() > MAX_SPEED
                        || input.getSpeedMultiplier() % SPEED_STEP != 0;

        final boolean hasInvalidBalance = input.getInitialBalance() <= 0;

        if (hasInvalidSpeed) {
            presenter.prepareFailView("Invalid speed. Must be 5x, 10x, 20x, or 30x.");
        }
        else if (hasInvalidBalance) {
            presenter.prepareFailView("Initial balance must be positive.");
        }
        else {
            presenter.prepareSuccessView(input);
        }
    }
}
