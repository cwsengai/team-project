package interface_adapter.setup_simulation;

import interface_adapter.ViewManagerModel;
import interface_adapter.simulated_trading.TradingViewModel;
import use_case.setup_simulation.SetupOutputBoundary;
import use_case.setup_simulation.SetupInputData; // 必须导入

public class SetupPresenter implements SetupOutputBoundary {

    // ✅ FIX 1: 将 private 改为 protected，让子类 FinalSetupPresenter 可以访问这些 View Model
    protected final ViewManagerModel viewManagerModel;
    protected final TradingViewModel tradingViewModel;
    protected final SetupViewModel setupViewModel;

    public SetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.tradingViewModel = tradingViewModel;
        this.setupViewModel = setupViewModel;
    }

    // ✅ FIX 2: 修正签名，让它接受 SetupInputData，消除 "does not override" 错误
    @Override
    public void prepareSuccessView(SetupInputData input) {
        // 基类的主要职责是触发 View 切换
        viewManagerModel.setActiveView(tradingViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        setupViewModel.setError(error);
        setupViewModel.firePropertyChanged();
    }
}