package interfaceadapter.setup_simulation;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.simulated_trading.TradingViewModel;
import usecase.setup_simulation.SetupOutputBoundary;
import usecase.setup_simulation.SetupInputData;

public class SetupPresenter implements SetupOutputBoundary {

    protected final ViewManagerModel viewManagerModel;
    protected final TradingViewModel tradingViewModel;
    protected final SetupViewModel setupViewModel;

    public SetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.tradingViewModel = tradingViewModel;
        this.setupViewModel = setupViewModel;
    }

    @Override
    public void prepareSuccessView(SetupInputData input) {
        viewManagerModel.setActiveView(tradingViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        setupViewModel.setError(error);
        setupViewModel.firePropertyChanged();
    }
}