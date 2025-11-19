package app;

import api.Api;

import data_access.AlphaVantageCompanyGateway;
import data_access.AlphaVantageFinancialStatementGateway;
import data_access.AlphaVantageNewsGateway;

import framework_and_driver.CompanyPage;

import interface_adapter.view_model.CompanyViewModel;
import interface_adapter.view_model.FinancialStatementViewModel;
import interface_adapter.view_model.NewsViewModel;

import interface_adapter.presenter.CompanyPresenter;
import interface_adapter.presenter.FinancialStatementPresenter;
import interface_adapter.presenter.NewsPresenter;

import interface_adapter.controller.CompanyController;
import interface_adapter.controller.FinancialStatementController;
import interface_adapter.controller.NewsController;

import use_case.company.*;
import use_case.financial_statement.*;
import use_case.news.*;

public class CompanyMain {

    public static void main(String[] args) {

        // ----------------------
        // API + GATEWAYS
        // ----------------------
        Api api = new Api("demo");   // Replace with real API key

        AlphaVantageCompanyGateway companyGateway =
                new AlphaVantageCompanyGateway(api);

        AlphaVantageFinancialStatementGateway fsGateway =
                new AlphaVantageFinancialStatementGateway(api);

        AlphaVantageNewsGateway newsGateway =
                new AlphaVantageNewsGateway(api);


        //view models
        CompanyViewModel companyVM = new CompanyViewModel();
        FinancialStatementViewModel fsVM = new FinancialStatementViewModel();
        NewsViewModel newsVM = new NewsViewModel();


        //ui
        CompanyPage ui = new CompanyPage(companyVM, fsVM, newsVM);


        //presenters
        CompanyPresenter companyPresenter =
                new CompanyPresenter(companyVM);

        FinancialStatementPresenter fsPresenter =
                new FinancialStatementPresenter(fsVM);

        NewsPresenter newsPresenter =
                new NewsPresenter(newsVM);


        //interactors
        CompanyInteractor companyInteractor =
                new CompanyInteractor(companyGateway, companyPresenter);

        FinancialStatementInteractor fsInteractor =
                new FinancialStatementInteractor(fsGateway, fsPresenter);

        NewsInteractor newsInteractor =
                new NewsInteractor(newsGateway, newsPresenter);


        //controllers
        CompanyController companyController =
                new CompanyController(companyInteractor);

        FinancialStatementController fsController =
                new FinancialStatementController(fsInteractor);

        NewsController newsController =
                new NewsController(newsInteractor);


        //inject controllers into UI
        ui.setControllers(companyController, fsController, newsController);

    }
}
