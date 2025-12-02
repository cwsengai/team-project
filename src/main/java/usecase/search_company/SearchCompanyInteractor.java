package usecase.search_company;

import java.util.List;

import entity.Company;

/**
 * Interactor for the Search Company use case.
 * Handles business rules and communicates results to the presenter.
 */
public class SearchCompanyInteractor implements SearchCompanyInputBoundary {

    private static final int MIN_QUERY_LENGTH = 2;

    private final SearchCompanyOutputBoundary presenter;
    private final SearchCompanyDataAccess dataAccess;

    /**
     * Constructs a SearchCompanyInteractor.
     *
     * @param dataAccess the data access interface for querying companies
     * @param presenter the presenter that formats and returns results
     */
    public SearchCompanyInteractor(SearchCompanyDataAccess dataAccess,
                                   SearchCompanyOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the search operation using the provided input data.
     *
     * @param inputData the search query input
     */
    @Override
    public void execute(SearchCompanyInputData inputData) {
        String query = inputData.getQuery();

        // Business rule: query must have at least MIN_QUERY_LENGTH characters.
        if (query == null || query.trim().length() < MIN_QUERY_LENGTH) {
            presenter.presentError("Search query must have at least 2 characters.");
            return;
        }

        try {
            List<Company> results = dataAccess.searchCompanies(query);
            SearchCompanyOutputData outputData =
                    new SearchCompanyOutputData(results);
            presenter.presentSearchResults(outputData);
        }
        catch (Exception ex) {
            presenter.presentError("Search failed: " + ex.getMessage());
        }
    }
}
