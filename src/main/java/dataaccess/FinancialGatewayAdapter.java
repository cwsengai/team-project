package dataaccess;

import entity.FinancialStatement;
import usecase.FinancialGateway;
import usecase.financial_statement.FinancialStatementGateway;
import java.util.List;

/**
 * Adapter that adapts FinancialStatementGateway to FinancialGateway interface
 */
public class FinancialGatewayAdapter implements FinancialGateway {
    
    private final FinancialStatementGateway financialStatementGateway;
    
    public FinancialGatewayAdapter(FinancialStatementGateway financialStatementGateway) {
        this.financialStatementGateway = financialStatementGateway;
    }
    
    @Override
    public FinancialStatement getFinancials(String ticker, String reportType) throws Exception {
        List<FinancialStatement> statements = financialStatementGateway.fetchFinancialStatements(ticker);
        if (statements != null && !statements.isEmpty()) {
            // Return the most recent financial statement
            return statements.get(0);
        }
        return null;
    }
}

