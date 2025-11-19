package use_case;

import entity.FinancialStatement;

/**
 * FinancialGateway defined the financial format of the company
 */
public interface FinancialGateway {

    FinancialStatement getFinancials(String ticker, String reportType);
}