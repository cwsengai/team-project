package usecase.financial_statement;

import java.util.List;
import entity.FinancialStatement;

public interface FinancialStatementOutputBoundary {
    void presentFinancialStatement(List<FinancialStatement> financialStatement);

    void presentError(String message);
}
