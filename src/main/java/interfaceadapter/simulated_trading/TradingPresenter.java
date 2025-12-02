package interfaceadapter.simulated_trading;

import java.text.DecimalFormat;

import usecase.simulated_trade.SimulatedTradeOutputBoundary;
import usecase.simulated_trade.SimulatedTradeOutputData;
import usecase.update_market.UpdateMarketOutputBoundary;
import usecase.update_market.UpdateMarketOutputData;

/**
 * Presenter responsible for updating the trading view model based on
 * market updates and simulated trade results.
 */
public class TradingPresenter implements UpdateMarketOutputBoundary,
        SimulatedTradeOutputBoundary {

    private final TradingViewModel viewModel;

    private final DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
    private final DecimalFormat pctFormat = new DecimalFormat("0.00%");

    /**
     * Constructs a TradingPresenter.
     *
     * @param viewModel        the trading view model
     */
    public TradingPresenter(TradingViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(UpdateMarketOutputData data) {
        final TradingState state = viewModel.getState();

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
        final TradingState state = viewModel.getState();
        state.setAvailableCash(moneyFormat.format(data.getNewBalance()));

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final TradingState state = viewModel.getState();
        state.setError(error);
        viewModel.firePropertyChanged();
    }
}
