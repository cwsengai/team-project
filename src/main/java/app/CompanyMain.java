package app;

import api.Api;
import data_access.config.EnvConfig;
import data_access.gateway.alphavantage.AlphaVantageCompanyGateway;
import framework_and_driver.CompanyPage;
import interface_adapter.controller.CompanyController;
import interface_adapter.presenter.CompanyPresenter;
import interface_adapter.view_model.CompanyViewModel;
import use_case.company.CompanyInputBoundary;
import use_case.company.CompanyInteractor;
import use_case.company.CompanyOutputBoundary;

public class CompanyMain {
    public static void main(String[] args) {

        String apiKey = EnvConfig.getAlphaVantageApiKey();
        if (apiKey.isEmpty()) {
            apiKey = "demo"; // Fallback to demo key
        }
        Api api = new Api(apiKey);
        AlphaVantageCompanyGateway gateway = new AlphaVantageCompanyGateway(api);

        CompanyViewModel vm = new CompanyViewModel();

        CompanyPage ui = new CompanyPage(
                vm,
                null // placeholder, controller set after presenter created
        );

        CompanyOutputBoundary presenter =
                new CompanyPresenter(vm, ui);

        CompanyInputBoundary interactor =
                new CompanyInteractor(gateway, presenter);

        CompanyController controller =
                new CompanyController(interactor);

        ui.setController(controller); // add setter to UI
    }
}
