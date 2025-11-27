package app;

import javax.swing.SwingUtilities;

import api.Api;
import data_access.AlphaVantagePriceGateway;

import framework_and_driver.CompanyPage;
import framework_and_driver.ChartViewAdapter;

import interface_adapter.controller.IntervalController;
import interface_adapter.presenter.PriceChartPresenter;

import interface_adapter.controller.CompanyController;
import interface_adapter.controller.FinancialStatementController;
import interface_adapter.controller.NewsController;

import interface_adapter.presenter.CompanyPresenter;
import interface_adapter.presenter.FinancialStatementPresenter;
import interface_adapter.presenter.NewsPresenter;

import interface_adapter.view_model.CompanyViewModel;
import interface_adapter.view_model.FinancialStatementViewModel;
import interface_adapter.view_model.NewsViewModel;

import data_access.AlphaVantageCompanyGateway;
import data_access.AlphaVantageFinancialStatementGateway;
import data_access.AlphaVantageNewsGateway;

import use_case.company.CompanyInteractor;
import use_case.financial_statement.FinancialStatementInteractor;
import use_case.news.NewsInteractor;
import use_case.price_chart.GetPriceByIntervalInteractor;
import use_case.price_chart.PriceDataAccessInterface;
import use_case.price_chart.PriceChartOutputBoundary;


public class CompanyMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // -----------------------------
            // API + GATEWAYS
            // -----------------------------
            Api api = new Api("demo");

            AlphaVantageCompanyGateway companyGateway =
                    new AlphaVantageCompanyGateway(api);

            AlphaVantageFinancialStatementGateway fsGateway =
                    new AlphaVantageFinancialStatementGateway(api);

            AlphaVantageNewsGateway newsGateway =
                    new AlphaVantageNewsGateway(api);

            PriceDataAccessInterface priceGateway =
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