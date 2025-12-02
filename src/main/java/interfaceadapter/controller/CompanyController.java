package interfaceadapter.controller;

import usecase.company.CompanyInputBoundary;
import usecase.company.CompanyInputData;

/**
 * Controller for handling company selection actions.
 * Receives user input from the interface layer and forwards it
 * to the corresponding use case interactor.
 */
public class CompanyController {

    private final CompanyInputBoundary interactor;

    /**
     * Constructs a CompanyController using the given interactor.
     *
     * @param interactor the input boundary for the company use case
     */
    public CompanyController(CompanyInputBoundary interactor) {

        this.interactor = interactor;
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
