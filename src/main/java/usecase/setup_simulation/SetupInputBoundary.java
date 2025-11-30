package usecase.setup_simulation;

/**
 * Boundary interface for the setup simulation use case.
 */
public interface SetupInputBoundary {

    /**
     * Executes the setup process with the given input data.
     *
     * @param inputData data required to initialize the simulation
     */
    void execute(SetupInputData inputData);
}
