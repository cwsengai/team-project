package app;

import java.util.Optional;
import javax.swing.SwingUtilities;

import api.Api;
import dataaccess.AlphaVantagePriceGateway;
import io.github.cdimascio.dotenv.Dotenv;

import frameworkanddriver.CompanyPage;
import frameworkanddriver.ChartViewAdapter;

import interfaceadapter.controller.IntervalController;
import interfaceadapter.presenter.PriceChartPresenter;

import interfaceadapter.controller.CompanyController;
import interfaceadapter.controller.FinancialStatementController;
import interfaceadapter.controller.NewsController;

import interfaceadapter.presenter.CompanyPresenter;
import interfaceadapter.presenter.FinancialStatementPresenter;
import interfaceadapter.presenter.NewsPresenter;

import interfaceadapter.view_model.CompanyViewModel;
import interfaceadapter.view_model.FinancialStatementViewModel;
import interfaceadapter.view_model.NewsViewModel;

import dataaccess.AlphaVantageCompanyGateway;
import dataaccess.AlphaVantageFinancialStatementGateway;
import dataaccess.AlphaVantageNewsGateway;

import usecase.company.CompanyInteractor;
import usecase.financial_statement.FinancialStatementInteractor;
import usecase.news.NewsInteractor;
import usecase.price_chart.GetPriceByIntervalInteractor;
import usecase.price_chart.PriceDataAccessInterface;
import usecase.price_chart.PriceChartOutputBoundary;


public class CompanyMain {

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
            CompanyViewModel companyVM = new CompanyViewModel();
            FinancialStatementViewModel fsVM = new FinancialStatementViewModel();
            NewsViewModel newsVM = new NewsViewModel();

            // -----------------------------
            // UI
            // -----------------------------
            CompanyPage ui = new CompanyPage(companyVM, fsVM, newsVM);

            // -----------------------------
            // CHART ADAPTER
            // -----------------------------
            ChartViewAdapter chartAdapter = new ChartViewAdapter(ui);

            // -----------------------------
            // CHART PRESENTER
            // -----------------------------
            PriceChartOutputBoundary pricePresenter =
                    new PriceChartPresenter(chartAdapter);

            // -----------------------------
            // INTERACTORS
            // -----------------------------
            CompanyInteractor companyInteractor =
                    new CompanyInteractor(companyGateway, new CompanyPresenter(companyVM));

            FinancialStatementInteractor fsInteractor =
                    new FinancialStatementInteractor(fsGateway, new FinancialStatementPresenter(fsVM));

            NewsInteractor newsInteractor =
                    new NewsInteractor(newsGateway, new NewsPresenter(newsVM));

            GetPriceByIntervalInteractor priceInteractor =
                    new GetPriceByIntervalInteractor(priceGateway, pricePresenter);

            // -----------------------------
            // CONTROLLERS
            // -----------------------------
            CompanyController companyController =
                    new CompanyController(companyInteractor);

            FinancialStatementController fsController =
                    new FinancialStatementController(fsInteractor);

            NewsController newsController =
                    new NewsController(newsInteractor);

            IntervalController intervalController =
                    new IntervalController(priceInteractor);

            // inject controllers into UI
            ui.setControllers(companyController, fsController, newsController, intervalController);

            ui.setVisible(true);
        });
    }
}