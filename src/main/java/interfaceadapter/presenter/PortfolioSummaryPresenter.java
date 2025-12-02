package interfaceadapter.presenter;

import interfaceadapter.view_model.PortfolioSummaryViewModel;
import usecase.portfolio_statistics.PortfolioStatisticsOutputBoundary;
import usecase.portfolio_statistics.PortfolioStatisticsOutputData;

public class PortfolioSummaryPresenter implements PortfolioStatisticsOutputBoundary {

    private PortfolioSummaryViewModel viewModel;
    private PortfolioStatisticsOutputData outputData;

    @Override
    public void present(PortfolioStatisticsOutputData outputData) {
        this.outputData = outputData;
        this.viewModel = new PortfolioSummaryViewModel(
            outputData.getTotalProfit(),
            outputData.getMaxGain(),
            outputData.getMaxDrawdown(),
            outputData.getTotalTrades(),
            outputData.getWinningTrades(),
            outputData.getLosingTrades(),
            outputData.getWinRate(),
            outputData.getTotalReturnRate(),
            outputData.getEarliestTrade(),
            outputData.getLatestTrade()
        );
    }

    public PortfolioSummaryViewModel getViewModel() {
        return viewModel;
    }

    public PortfolioStatisticsOutputData getOutputData() {
        return outputData;
    }
}
