package usecase.update_market;

public interface UpdateMarketOutputBoundary {
    void prepareSuccessView(UpdateMarketOutputData outputData);
    void prepareFailView(String error);
}