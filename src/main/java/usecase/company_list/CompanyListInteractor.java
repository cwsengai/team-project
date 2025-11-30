package usecase.company_list;

import entity.Company;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactor for Company List use case.
 * Contains the business logic for loading and displaying the top companies.
 */
public class CompanyListInteractor implements CompanyListInputBoundary {
    private final CompanyListDataAccess dataAccess;
    private final CompanyListOutputBoundary presenter;

    public CompanyListInteractor(CompanyListDataAccess dataAccess,
                                 CompanyListOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(CompanyListInputData inputData) {
        try {
            // Get companies' data from data access layer
            List<Company> companies = dataAccess.getCompanyList();

            // Apply business rule: Limit the number of companies to 100
            if (companies.size() > 100) {
                companies = companies.subList(0, 100);
            }

            // Rules: filter out the invalid data
            companies = companies.stream()
                    .filter(c -> c.getMarketCapitalization() > 0)
                    .collect(Collectors.toList());

            // Create output data
            CompanyListOutputData outputData = new CompanyListOutputData(companies, true);
            presenter.presentCompanyList(outputData);

        } catch (Exception e) {
            presenter.presentError("Failed to load companies: " + e.getMessage());
        }
    }
}