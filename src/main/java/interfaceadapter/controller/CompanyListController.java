package interfaceadapter.controller;

import usecase.company_list.CompanyListInputBoundary;
import usecase.company_list.CompanyListInputData;

/**
 * Controller for Company List use case.
 * Handles user actions to load the top companies list.
 */
public class CompanyListController {
    private final CompanyListInputBoundary interactor;

    public CompanyListController(CompanyListInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load the list of top companies.
     * This triggers the use case to fetch and display companies.
     */
    public void loadCompanyList() {
        // Create empty input data (no parameters needed for loading full list)
        CompanyListInputData inputData = new CompanyListInputData();
        interactor.execute();
    }
}
