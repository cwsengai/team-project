package usecase.setup_simulation;

/**
 * Interactor for handling the setup simulation use case.
 */
public class SetupInteractor implements SetupInputBoundary {

    private static final int MIN_SPEED = 5;
    private static final int MAX_SPEED = 30;
    private static final int SPEED_STEP = 5;

    private final SetupOutputBoundary presenter;

    public SetupInteractor(SetupOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(SetupInputData input) {

        final boolean hasInvalidSpeed =
                input.speedMultiplier() < MIN_SPEED
                        || input.speedMultiplier() > MAX_SPEED
                        || input.speedMultiplier() % SPEED_STEP != 0;

        final boolean hasInvalidBalance = input.initialBalance() <= 0;

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
