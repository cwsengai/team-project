package usecase.company;

import entity.Company;

public class CompanyInteractor implements CompanyInputBoundary {
    private final CompanyGateway gateway;
    private final CompanyOutputBoundary presenter;

    public CompanyInteractor(CompanyGateway gateway, CompanyOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(CompanyInputData data) {
        final String symbol = data.getSymbol();
        final Company company = gateway.fetchOverview(symbol);

        if (company.getName() == null || company.getName().isBlank()) {
            presenter.presentError("Company not found: " + symbol);
        }
        else {
            final CompanyOutputData output = new CompanyOutputData(
                    company.getSymbol(),
                    company.getName(),
                    company.getSector(),
                    company.getIndustry(),
                    company.getDescription()
            );

            presenter.presentCompany(output);
        }
    }

}