package usecase.company_list;

import java.util.List;
import java.util.stream.Collectors;

import entity.Company;

/**
 * Interactor for the Company List use case.
 * Contains the business logic for loading and displaying company information.
 */
public class CompanyListInteractor implements CompanyListInputBoundary {

    /** Maximum number of companies allowed for display. */
    private static final int MAX_COMPANIES = 100;

    private final CompanyListDataAccess dataAccess;
    private final CompanyListOutputBoundary presenter;

    /**
     * Creates a new CompanyListInteractor.
     *
     * @param dataAccess the data access interface used to retrieve company data
     * @param presenter the output boundary responsible for formatting the result
     */
    public CompanyListInteractor(CompanyListDataAccess dataAccess,
                                 CompanyListOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the Company List use case by retrieving company data,
     * applying business rules, and forwarding the result to the presenter.
     *
     */
    @Override
    public void execute() {

        try {
            // Retrieve companies from data access layer
            List<Company> companies = dataAccess.getCompanyList();

            // Apply rule: limit to the maximum allowed number
            if (companies.size() > MAX_COMPANIES) {
                companies = companies.subList(0, MAX_COMPANIES);
            }

            // Filter out invalid data (negative or zero market cap)
            companies = companies.stream()
                    .filter(company -> company.getMarketCapitalization() > 0)
                    .collect(Collectors.toList());

            // Prepare output data
            CompanyListOutputData outputData = new CompanyListOutputData(companies, true);
            presenter.presentCompanyList(outputData);

        }
        catch (RuntimeException ex) {
            presenter.presentError("Failed to load companies: " + ex.getMessage());
        }
    }
}
