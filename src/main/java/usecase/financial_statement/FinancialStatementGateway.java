package usecase.financial_statement;

import java.util.List;

import entity.FinancialStatement;

/**
 * Provides access to financial statement data sources.
 */
public interface FinancialStatementGateway {
    /**
     * Retrieves the financial statements for the given company symbol.
     *
     * @param symbol the company's ticker symbol
     * @return a list of financial statements
     */
    List<FinancialStatement> fetchFinancialStatements(String symbol);
}