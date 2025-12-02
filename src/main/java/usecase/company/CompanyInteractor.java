package usecase.company;

import entity.Company;

public record CompanyInteractor(CompanyGateway gateway,
                                CompanyOutputBoundary presenter) implements CompanyInputBoundary {

    @Override
    public void execute(CompanyInputData data) {
        final String symbol = data.symbol();
        final Company company = gateway.fetchOverview(symbol);

        if (company.getName() == null || company.getName().isBlank()) {
            presenter.presentError("Company not found: " + symbol);
        } else {
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
