package app;

import java.util.UUID;

import app.gateway.SupabasePortfolioGatewayAdapter;
import app.gateway.SupabaseTradeGatewayAdapter;
import interfaceadapter.presenter.PortfolioSummaryPresenter;
import interfaceadapter.view_model.PortfolioSummaryViewModel;
import usecase.portfolio_statistics.PortfolioStatisticsInteractor;

public class PortfolioSummaryAssembler {

    public static PortfolioSummaryViewModel buildSummary(UUID userId) {
        SupabaseTradeGatewayAdapter tradeGateway = new SupabaseTradeGatewayAdapter();
        SupabasePortfolioGatewayAdapter portfolioGateway = new SupabasePortfolioGatewayAdapter();
        PortfolioSummaryPresenter presenter = new PortfolioSummaryPresenter();

        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor(
            tradeGateway, portfolioGateway, presenter
        );

        interactor.requestPortfolioSummary(userId);
        return presenter.getViewModel();
    }

    public static usecase.portfolio_statistics.PortfolioStatisticsOutputData buildOutputData(UUID userId) {
        SupabaseTradeGatewayAdapter tradeGateway = new SupabaseTradeGatewayAdapter();
        SupabasePortfolioGatewayAdapter portfolioGateway = new SupabasePortfolioGatewayAdapter();
        PortfolioSummaryPresenter presenter = new PortfolioSummaryPresenter();

        PortfolioStatisticsInteractor interactor = new PortfolioStatisticsInteractor(
            tradeGateway, portfolioGateway, presenter
        );

        interactor.requestPortfolioSummary(userId);
        return presenter.getOutputData();
    }
}
