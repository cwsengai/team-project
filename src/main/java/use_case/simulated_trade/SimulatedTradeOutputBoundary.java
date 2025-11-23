package use_case.simulated_trade;

public interface SimulatedTradeOutputBoundary {
    void prepareSuccessView(SimulatedTradeOutputData outputData);
    void prepareFailView(String error);
}