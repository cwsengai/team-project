package app;

import java.util.Optional;

import javax.swing.SwingUtilities;

import api.AlphaVantagePriceGateway;
import api.Api;
import dataaccess.AlphaVantageCompanyGateway;
import dataaccess.AlphaVantageFinancialStatementGateway;
import dataaccess.AlphaVantageNewsGateway;
import frameworkanddriver.ChartViewAdapter;
import frameworkanddriver.CompanyPage;
import interfaceadapter.IntervalController;
import interfaceadapter.PriceChartPresenter;
import interfaceadapter.controller.CompanyController;
import interfaceadapter.controller.FinancialStatementController;
import interfaceadapter.controller.NewsController;
import interfaceadapter.presenter.CompanyPresenter;
import interfaceadapter.presenter.FinancialStatementPresenter;
import interfaceadapter.presenter.NewsPresenter;
import interfaceadapter.view_model.CompanyViewModel;
import interfaceadapter.view_model.FinancialStatementViewModel;
import interfaceadapter.view_model.NewsViewModel;
import io.github.cdimascio.dotenv.Dotenv;
import usecase.GetPriceByIntervalInteractor;
import usecase.PriceChartOutputBoundary;
import usecase.PriceDataAccessInterface;
import usecase.company.CompanyInteractor;
import usecase.financial_statement.FinancialStatementInteractor;
import usecase.news.NewsInteractor;

public class CompanyMain {

    /**
     * Application entry point. Initializes the UI, API gateways,
     * interactors, presenters, controllers, and wires all layers
     * of the clean architecture before launching the dashboard.
     *
     * @param args command-line arguments, unused in this application
     */
    public static void main(String[] args) {

        final Dotenv dotenv = Dotenv.load();
        final String apiKey = Optional.ofNullable(dotenv.get("ALPHA_VANTAGE_API_KEY"))
                .filter(key -> !key.isEmpty())
                .orElse("demo");

        SwingUtilities.invokeLater(() -> {

            // -----------------------------
            // API + GATEWAYS
            // -----------------------------
            final Api api = new Api(apiKey);

            final AlphaVantageCompanyGateway companyGateway =
                    new AlphaVantageCompanyGateway(api);

            final AlphaVantageFinancialStatementGateway fsGateway =
                    new AlphaVantageFinancialStatementGateway(api);

            final AlphaVantageNewsGateway newsGateway =
                    new AlphaVantageNewsGateway(api);

            final PriceDataAccessInterface priceGateway =
                    new AlphaVantagePriceGateway();

            // -----------------------------
            // VIEW MODELS
            // -----------------------------
            final CompanyViewModel companyvm = new CompanyViewModel();
            final FinancialStatementViewModel fsvm = new FinancialStatementViewModel();
            final NewsViewModel newsvm = new NewsViewModel();

            // -----------------------------
            // UI
            // -----------------------------
            final CompanyPage ui = new CompanyPage(companyvm, fsvm, newsvm);

            // -----------------------------
            // CHART ADAPTER
            // -----------------------------
            final ChartViewAdapter chartAdapter = new ChartViewAdapter(ui);

            // -----------------------------
            // CHART PRESENTER
            // -----------------------------
            final PriceChartOutputBoundary pricePresenter =
                    new PriceChartPresenter(chartAdapter);

            // -----------------------------
            // INTERACTORS
            // -----------------------------
            final CompanyInteractor companyInteractor =
                    new CompanyInteractor(companyGateway, new CompanyPresenter(companyvm));

            final FinancialStatementInteractor fsInteractor =
                    new FinancialStatementInteractor(fsGateway, new FinancialStatementPresenter(fsvm));

            final NewsInteractor newsInteractor =
                    new NewsInteractor(newsGateway, new NewsPresenter(newsvm));

            final GetPriceByIntervalInteractor priceInteractor =
                    new GetPriceByIntervalInteractor(priceGateway, pricePresenter);

            // -----------------------------
            // CONTROLLERS
            // -----------------------------
            final CompanyController companyController =
                    new CompanyController(companyInteractor);

            final FinancialStatementController fsController =
                    new FinancialStatementController(fsInteractor);

            final NewsController newsController =
                    new NewsController(newsInteractor);

            final IntervalController intervalController =
                    new IntervalController(priceInteractor);

            // inject controllers into UI
            ui.setControllers(companyController, fsController, newsController, intervalController);

            ui.setVisible(true);
        });
    }
}
