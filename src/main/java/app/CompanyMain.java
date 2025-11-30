package app;

import javax.swing.SwingUtilities;

import api.Api;
import dataaccess.AlphaVantagePriceGateway;

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