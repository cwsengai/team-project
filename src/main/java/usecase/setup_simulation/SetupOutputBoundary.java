package usecase.setup_simulation;

/**
 * Output boundary for the setup simulation use case.
 * Defines methods for presenting success or failure responses.
 */
public interface SetupOutputBoundary {

    /**
     * Prepares the success view for valid setup input.
     *
     * @param input the validated setup data
     */
    void prepareSuccessView(SetupInputData input);

    /**
     * Prepares the failure view when setup validation fails.
     *
     * @param error the error message describing the failure
     */
    void prepareFailView(String error);
}
