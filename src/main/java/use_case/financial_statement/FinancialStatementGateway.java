package use_case.financial_statement;

import java.util.List;
import entity.FinancialStatement;

public interface FinancialStatementGateway {
    List<FinancialStatement> fetchFinancialStatements(String symbol);
}
