package usecase.financial_statement;


/**
 * Handles input for the financial statement use case.
 */
public interface FinancialStatementInputBoundary {
    /**
     * Executes the use case with the provided input data.
     *
     * @param data the input data for the use case
     */
    void execute(FinancialStatementInputData data);
}

