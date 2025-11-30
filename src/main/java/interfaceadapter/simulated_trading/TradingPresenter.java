package interfaceadapter.simulated_trading;

import usecase.simulated_trade.SimulatedTradeOutputBoundary;
import usecase.simulated_trade.SimulatedTradeOutputData;
import usecase.update_market.UpdateMarketOutputBoundary;
import usecase.update_market.UpdateMarketOutputData;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.setup_simulation.SetupViewModel;

import java.text.DecimalFormat;

public class TradingPresenter implements UpdateMarketOutputBoundary, SimulatedTradeOutputBoundary {

    private final TradingViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final SetupViewModel setupViewModel;

    private final DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
    private final DecimalFormat pctFormat = new DecimalFormat("0.00%");

    public TradingPresenter(TradingViewModel viewModel, ViewManagerModel viewManagerModel, SetupViewModel setupViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.setupViewModel = setupViewModel;
    }

    public void prepareGoBackView() {
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareSuccessView(UpdateMarketOutputData data) {
        TradingState state = viewModel.getState();

        state.setCurrentPrice(moneyFormat.format(data.getCurrentPrice()));
        state.setAvailableCash(moneyFormat.format(data.getAvailableCash()));
        state.setTotalProfit(moneyFormat.format(data.getTotalEquity()));
        state.setTotalReturnRate(pctFormat.format(data.getTotalReturnRate()));
        state.setMaxDrawdown(moneyFormat.format(data.getMaxDrawdown()));

        state.setTotalTrades(String.valueOf(data.getTotalTrades()));
        state.setWinningTrades(String.valueOf(data.getWinningTrades()));
        state.setLosingTrades(String.valueOf(data.getLosingTrades()));
        state.setMaxGain(moneyFormat.format(data.getMaxGain()));
        state.setWinRate(pctFormat.format(data.getWinRate()));

        state.setPositions(data.getPositions());
        state.setChartData(data.getChartData());
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareSuccessView(SimulatedTradeOutputData data) {
        TradingState state = viewModel.getState();

        state.setAvailableCash(moneyFormat.format(data.getNewBalance()));

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        TradingState state = viewModel.getState();
        state.setError(error);
        viewModel.firePropertyChanged();
    }
}