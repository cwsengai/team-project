package app;

import java.util.UUID;

import app.gateway.SupabasePortfolioGatewayAdapter;
import app.gateway.SupabaseTradeGatewayAdapter;
import interfaceadapter.presenter.PortfolioSummaryPresenter;
import interfaceadapter.view_model.PortfolioSummaryViewModel;
import usecase.portfolio_statistics.PortfolioStatisticsInteractor;
import usecase.portfolio_statistics.PortfolioStatisticsOutputData;

/**
 * Assembler for creating and executing the portfolio summary workflow.
 *
 * <p>This class wires together the gateway adapters, presenter,
 * and interactor required to generate summary data or view models
 * for a user's portfolio.</p>
 */
public class PortfolioSummaryAssembler {

    /**
     * Builds and executes the portfolio summary workflow,
     * returning the resulting ViewModel.
     *
     * @param userId the ID of the user requesting the summary
     * @return the generated PortfolioSummaryViewModel
     */
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

    /**
     * Builds and executes the portfolio summary workflow,
     * returning the raw output data from the presenter.
     *
     * @param userId the ID of the user requesting output data
     * @return the generated PortfolioStatisticsOutputData
     */
    public static PortfolioStatisticsOutputData buildOutputData(UUID userId) {
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
