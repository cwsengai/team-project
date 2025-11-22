package interface_adapter.simulated_trading;

import use_case.simulated_trade.SimulatedTradeOutputBoundary;
import use_case.simulated_trade.SimulatedTradeOutputData;
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

        // 1. 格式化和更新货币/比率字段 (Currency/Rate Fields)
        state.setCurrentPrice(moneyFormat.format(data.getCurrentPrice()));
        state.setAvailableCash(moneyFormat.format(data.getAvailableCash()));
        state.setTotalProfit(moneyFormat.format(data.getTotalEquity()));
        state.setTotalReturnRate(pctFormat.format(data.getTotalReturnRate()));
        state.setMaxDrawdown(moneyFormat.format(data.getMaxDrawdown()));

        // 2. 格式化和更新统计计数 (Statistical Counts)
        state.setTotalTrades(String.valueOf(data.getTotalTrades())); // 计数值 (int -> String)
        state.setWinningTrades(String.valueOf(data.getWinningTrades())); // 计数值
        state.setLosingTrades(String.valueOf(data.getLosingTrades()));   // 计数值
        state.setMaxGain(moneyFormat.format(data.getMaxGain()));        // 金额
        state.setWinRate(pctFormat.format(data.getWinRate()));          // 百分比

        // 3. 更新持仓和图表
        state.setPositions(data.getPositions());
        state.setChartData(data.getChartData());
        state.setError(null);

        // 4. 通知 View
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