package use_case.setup_simulation;

public interface SetupOutputBoundary {
    void prepareSuccessView(SetupInputData input);
    void prepareFailView(String error);
}