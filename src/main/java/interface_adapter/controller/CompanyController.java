package interface_adapter.controller;

import use_case.company.CompanyInputBoundary;


public class CompanyController {

    private final CompanyInputBoundary interactor;

    public CompanyController(CompanyInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onCompanySelected(String symbol) {
        interactor.fetchCompany(symbol);
    }
}
