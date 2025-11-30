package interfaceadapter.controller;

import usecase.company.CompanyInputBoundary;


public class CompanyController {

    private final CompanyInputBoundary interactor;

    public CompanyController(CompanyInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onCompanySelected(String symbol) {
        interactor.fetchCompany(symbol);
    }
}
