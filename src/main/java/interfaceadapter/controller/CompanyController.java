package interfaceadapter.controller;

import usecase.company.CompanyInputBoundary;
import usecase.company.CompanyInputData;

/**
 * Controller for handling company selection actions.
 * Receives user input from the interface layer and forwards it
 * to the corresponding use case interactor.
 */
public record CompanyController(CompanyInputBoundary interactor) {

    /**
     * Constructs a CompanyController using the given interactor.
     *
     * @param interactor the input boundary for the company use case
     */
    public CompanyController {

    }

    /**
     * Triggers the company use case when a company symbol is selected.
     *
     * @param symbol the stock ticker symbol selected by the user
     */
    public void onCompanySelected(String symbol) {
        final CompanyInputData data = new CompanyInputData(symbol);
        interactor.execute(data);
    }

}
