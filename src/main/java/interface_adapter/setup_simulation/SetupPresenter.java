package interface_adapter.setup_simulation;

import interface_adapter.ViewManagerModel;
import interface_adapter.simulated_trading.TradingViewModel;
import use_case.setup_simulation.SetupOutputBoundary;

public class SetupPresenter implements SetupOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final TradingViewModel tradingViewModel;
    private final SetupViewModel setupViewModel;

    public SetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.tradingViewModel = tradingViewModel;
        this.setupViewModel = setupViewModel;
    }

    @Override
    public void prepareSuccessView() {
        viewManagerModel.setActiveView(tradingViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        setupViewModel.setError(error);
        setupViewModel.firePropertyChanged();
    }
}