package interface_adapter.simulated_trading;

import use_case.simulated_trade.SimulatedTradeOutputBoundary;
import use_case.simulated_trade.SimulatedTradeOutputData;
import interface_adapter.ViewManagerModel;
import interface_adapter.setup_simulation.SetupViewModel;
import use_case.update_market.UpdateMarketOutputBoundary;
import use_case.update_market.UpdateMarketOutputData;

import java.text.DecimalFormat;

public class TradingPresenter implements UpdateMarketOutputBoundary, SimulatedTradeOutputBoundary {

    private final TradingViewModel viewModel;

    private final DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
    private final DecimalFormat pctFormat = new DecimalFormat("0.00%");

    public TradingPresenter(TradingViewModel viewModel) {
        this.viewModel = viewModel;
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