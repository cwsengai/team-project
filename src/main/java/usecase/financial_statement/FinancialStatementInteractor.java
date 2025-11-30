package usecase.financial_statement;

import entity.FinancialStatement;
import java.util.List;


public class FinancialStatementInteractor implements FinancialStatementInputBoundary{
    private final FinancialStatementGateway gateway;
    private final FinancialStatementOutputBoundary presenter;

    public FinancialStatementInteractor(FinancialStatementGateway gateway,
                                        FinancialStatementOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void fetchFinancialStatement(String symbol) {
        List<FinancialStatement> statements = gateway.fetchFinancialStatements(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No financial statements found for: " + symbol);
            return;
        }

        presenter.presentFinancialStatement(statements);

    }


}
