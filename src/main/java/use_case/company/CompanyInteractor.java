package use_case.company;

import entity.Company;

public class CompanyInteractor implements CompanyInputBoundary {
    private final CompanyGateway gateway;
    private final CompanyOutputBoundary presenter;

    public CompanyInteractor(CompanyGateway gateway, CompanyOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void fetchCompany(String symbol) {
        Company company = gateway.fetchOverview(symbol);
        if (company == null) {
            presenter.presentError("Unable to fetch company: " + symbol);
            return;
        }
        presenter.presentCompany(company);
    }
}
