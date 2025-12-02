package interfaceadapter.setup_simulation;

import interfaceadapter.simulated_trading.TradingViewModel;
import interfaceadapter.view_model.ViewManagerModel;
import usecase.setup_simulation.SetupInputData;
import usecase.setup_simulation.SetupOutputBoundary;

public class SetupPresenter implements SetupOutputBoundary {

    protected final ViewManagerModel viewManagerModel;
    protected final SetupViewModel setupViewModel;

    public SetupPresenter(ViewManagerModel viewManagerModel,
                          SetupViewModel setupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.setupViewModel = setupViewModel;
    }

    @Override
    public void prepareSuccessView(SetupInputData input) {
        viewManagerModel.setActiveView(TradingViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        setupViewModel.setError(error);
        setupViewModel.firePropertyChanged();
    }
}
