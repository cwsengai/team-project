package usecase.financial_statement;

/**
 * Output boundary for the financial statement use case.
 * Implementations receive formatted financial statement data
 * or an error message to display to the user interface.
 */
public interface FinancialStatementOutputBoundary {
    /**
     * Presents the financial statement output data.
     *
     * @param data the processed financial statement data to display
     */
    void presentFinancialStatement(FinancialStatementOutputData data);

    /**
     * Presents an error message when financial statement retrieval fails.
     *
     * @param message the error message describing the failure
     */
    void presentError(String message);
}
