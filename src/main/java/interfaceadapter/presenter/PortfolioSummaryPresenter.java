package interfaceadapter.presenter;

import interfaceadapter.view_model.PortfolioSummaryViewModel;
import usecase.portfolio_statistics.PortfolioStatisticsOutputBoundary;
import usecase.portfolio_statistics.PortfolioStatisticsOutputData;

public class PortfolioSummaryPresenter implements PortfolioStatisticsOutputBoundary {

    private PortfolioSummaryViewModel viewModel;

    @Override
    public void present(PortfolioStatisticsOutputData outputData) {
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

}
