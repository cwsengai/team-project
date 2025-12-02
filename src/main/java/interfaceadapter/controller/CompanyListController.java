package interfaceadapter.controller;

import usecase.company_list.CompanyListInputBoundary;

/**
 * Controller for Company List use case.
 * Handles user actions to load the top companies list.
 * @param interactor the input boundary for the company list use case
 */
public record CompanyListController(CompanyListInputBoundary interactor) {

    /**
     * Load the list of top companies.
     * This triggers the use case to fetch and display companies.
     */
    public void loadCompanyList() {
        // Create empty input data (no parameters needed for loading full list)
        interactor.execute();
    }
}
