package interfaceadapter.controller;

import usecase.search_company.SearchCompanyInputBoundary;
import usecase.search_company.SearchCompanyInputData;

/**
 * Controller for Search Company use case.
 * Handles user actions to search for companies.
 */
public record SearchCompanyController(SearchCompanyInputBoundary interactor) {

    /**
     * Search for companies matching the query.
     *
     * @param query Search string (company name or ticker)
     */
    public void searchCompany(String query) {
        System.out.println("🎮 Controller: Searching for '" + query + "'");
        SearchCompanyInputData inputData = new SearchCompanyInputData(query);
        interactor.execute(inputData);
    }
}
