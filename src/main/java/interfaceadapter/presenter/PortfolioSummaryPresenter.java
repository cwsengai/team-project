package interfaceadapter.presenter;

import interfaceadapter.view_model.PortfolioSummaryViewModel;
import usecase.portfolio_statistics.PortfolioStatisticsOutputBoundary;
import usecase.portfolio_statistics.PortfolioStatisticsOutputData;

public class PortfolioSummaryPresenter implements PortfolioStatisticsOutputBoundary {

    private PortfolioSummaryViewModel viewModel;

    @Override
    public void present(PortfolioStatisticsOutputData outputData) {
        this.viewModel = new PortfolioSummaryViewModel(
            outputData.totalProfit(),
            outputData.maxGain(),
            outputData.maxDrawdown(),
            outputData.totalTrades(),
            outputData.winningTrades(),
            outputData.losingTrades(),
            outputData.winRate(),
            outputData.totalReturnRate(),
            outputData.earliestTrade(),
            outputData.latestTrade()
        );
    }

    public PortfolioSummaryViewModel getViewModel() {
        return viewModel;
    }

}
