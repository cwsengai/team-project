package use_case.setup_simulation;

public interface SetupOutputBoundary {
    void prepareSuccessView();
    void prepareFailView(String error);
}