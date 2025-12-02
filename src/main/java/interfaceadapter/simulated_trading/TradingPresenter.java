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

        state.setCurrentPrice(moneyFormat.format(data.currentPrice()));
        state.setAvailableCash(moneyFormat.format(data.availableCash()));
        state.setTotalProfit(moneyFormat.format(data.totalEquity()));
        state.setTotalReturnRate(pctFormat.format(data.totalReturnRate()));
        state.setMaxDrawdown(moneyFormat.format(data.maxDrawdown()));
        state.setTotalTrades(String.valueOf(data.totalTrades()));
        state.setWinningTrades(String.valueOf(data.winningTrades()));
        state.setLosingTrades(String.valueOf(data.losingTrades()));
        state.setMaxGain(moneyFormat.format(data.maxGain()));
        state.setWinRate(pctFormat.format(data.winRate()));
        state.setPositions(data.positions());
        state.setChartData(data.chartData());
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareSuccessView(SimulatedTradeOutputData data) {
        final TradingState state = viewModel.getState();
        state.setAvailableCash(moneyFormat.format(data.newBalance()));

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
