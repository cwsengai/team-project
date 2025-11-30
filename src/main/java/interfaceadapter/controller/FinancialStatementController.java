package interfaceadapter.controller;

import usecase.financial_statement.FinancialStatementInputBoundary;

public class FinancialStatementController {
    private final FinancialStatementInputBoundary interactor;

    public FinancialStatementController(FinancialStatementInputBoundary interactor) {

        this.interactor = interactor;
    }

    public void onFinancialRequest(String symbol) {
        interactor.fetchFinancialStatement(symbol);
    }
}

