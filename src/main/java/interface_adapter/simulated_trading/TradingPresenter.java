package interface_adapter.simulated_trading;

import use_case.simulated_trade.SimulatedTradeOutputBoundary;
import use_case.simulated_trade.SimulatedTradeOutputData;
import use_case.update_market.UpdateMarketOutputBoundary;
import use_case.update_market.UpdateMarketOutputData;

import java.text.DecimalFormat;

public class TradingPresenter implements UpdateMarketOutputBoundary, SimulatedTradeOutputBoundary {

    private final TradingViewModel viewModel;

    // Formatters for currency and percentages
    private final DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
    private final DecimalFormat pctFormat = new DecimalFormat("0.00%");

    public TradingPresenter(TradingViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Called every second by the market update use case.
     * Formats raw doubles into displayable strings and updates the State.
     */
    @Override
    public void prepareSuccessView(UpdateMarketOutputData data) {
        TradingState state = viewModel.getState();

        // Format and update fields
        state.setCurrentPrice(moneyFormat.format(data.getCurrentPrice()));
        state.setAvailableCash(moneyFormat.format(data.getAvailableCash()));

        // Note: Displaying Total Equity as "Total Profit" area based on Figma layout
        state.setTotalProfit(moneyFormat.format(data.getTotalEquity()));

        state.setTotalReturnRate(pctFormat.format(data.getTotalReturnRate()));
        state.setMaxDrawdown(moneyFormat.format(data.getMaxDrawdown()));

        // Update chart data points
        state.setChartData(data.getChartData());

        // Clear errors
        state.setError(null);

        // Notify View
        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    /**
     * Called after a successful buy/sell trade.
     */
    @Override
    public void prepareSuccessView(SimulatedTradeOutputData data) {
        TradingState state = viewModel.getState();

        // Update cash balance immediately
        state.setAvailableCash(moneyFormat.format(data.getNewBalance()));

        viewModel.setState(state);
        viewModel.firePropertyChanged();
    }

    /**
     * Called when an error occurs (e.g., insufficient funds).
     */
    @Override
    public void prepareFailView(String error) {
        TradingState state = viewModel.getState();
        state.setError(error); // This triggers the popup in View
        viewModel.firePropertyChanged();
    }
}