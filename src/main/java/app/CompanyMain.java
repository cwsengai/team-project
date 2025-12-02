package app;

import javax.swing.SwingUtilities;

import api.Api;
import dataaccess.AlphaVantageCompanyGateway;
import dataaccess.AlphaVantageFinancialStatementGateway;
import dataaccess.AlphaVantageNewsGateway;
import dataaccess.AlphaVantagePriceGateway;
import dataaccess.EnvConfig;
import frameworkanddriver.ChartViewAdapter;
import frameworkanddriver.CompanyPage;
import interfaceadapter.controller.CompanyController;
import interfaceadapter.controller.FinancialStatementController;
import interfaceadapter.controller.IntervalController;
import interfaceadapter.controller.NewsController;
import interfaceadapter.presenter.CompanyPresenter;
import interfaceadapter.presenter.FinancialStatementPresenter;
import interfaceadapter.presenter.NewsPresenter;
import interfaceadapter.presenter.PriceChartPresenter;
import interfaceadapter.view_model.CompanyViewModel;
import interfaceadapter.view_model.FinancialStatementViewModel;
import interfaceadapter.view_model.NewsViewModel;
import usecase.company.CompanyInteractor;
import usecase.financial_statement.FinancialStatementInteractor;
import usecase.news.NewsInteractor;
import usecase.price_chart.GetPriceByIntervalInteractor;
import usecase.price_chart.PriceChartOutputBoundary;
import usecase.price_chart.PriceDataAccessInterface;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class CompanyMain {

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:FinalLocalVariable", "checkstyle:AbbreviationAsWordInName", "checkstyle:CommentsIndentation", "checkstyle:LambdaBodyLength", "checkstyle:UncommentedMain", "checkstyle:MissingJavadocMethod"})
    public static void main(String[] args) {
        final EnvConfig envConfig = new EnvConfig();
        final String apiKey = envConfig.getAlphaVantageApiKey();

        String preloadedSymbol = null;

        if (args != null && args.length > 0) {
            preloadedSymbol = args[0];
            System.out.println("Starting CompanyPage with preloaded symbol: " + preloadedSymbol);
        }

        final String symbolForLambda = preloadedSymbol;

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
            final CompanyViewModel companyVM = new CompanyViewModel();
            final FinancialStatementViewModel fsVM = new FinancialStatementViewModel();
            final NewsViewModel newsVM = new NewsViewModel();

            // -----------------------------
            // UI
            // -----------------------------
            final CompanyPage ui = new CompanyPage(companyVM, fsVM, newsVM);

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
                    new CompanyInteractor(companyGateway, new CompanyPresenter(companyVM));

            final FinancialStatementInteractor fsInteractor =
                    new FinancialStatementInteractor(fsGateway, new FinancialStatementPresenter(fsVM));

            final NewsInteractor newsInteractor =
                    new NewsInteractor(newsGateway, new NewsPresenter(newsVM));

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

            if (symbolForLambda != null) {
                ui.setInitialSymbol(symbolForLambda);
            }
            ui.setVisible(true);
        });
    }
}
