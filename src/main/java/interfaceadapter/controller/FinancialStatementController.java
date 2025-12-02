package interfaceadapter.controller;

import usecase.financial_statement.FinancialStatementInputBoundary;
import usecase.financial_statement.FinancialStatementInputData;

/**
 * Controller responsible for handling financial statement requests.
 * Receives user actions from the interface layer and forwards them
 * to the financial statement use case interactor.
 */
public record FinancialStatementController(FinancialStatementInputBoundary interactor) {
    /**
     * Constructs a FinancialStatementController with the given interactor.
     *
     * @param interactor the input boundary for the financial statement use case
     */
    public FinancialStatementController {

    }

    /**
     * Initiates a financial statement retrieval request for the given symbol.
     *
     * @param symbol the stock ticker symbol for which financial data is requested
     */
    public void onFinancialRequest(String symbol) {
        final FinancialStatementInputData data = new FinancialStatementInputData(symbol);

        interactor.execute(data);
    }
}

