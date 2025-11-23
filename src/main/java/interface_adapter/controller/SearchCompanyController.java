package interface_adapter.controller;

import use_case.search_company.SearchCompanyInputBoundary;
import use_case.search_company.SearchCompanyInputData;
import use_case.search_company.SearchCompanyOutputBoundary;

public class SearchCompanyController {
    private SearchCompanyInputBoundary interactor;

    public SearchCompanyController (SearchCompanyInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Search for companies matching the query.
     * @param query Search query (company name or symbol)
     */
    public void searchCompany(String query) {
        SearchCompanyInputData inputData = new SearchCompanyInputData(query);
        interactor.execute(inputData);
    }
}
