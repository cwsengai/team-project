package usecase.financial_statement;

import java.util.List;

public class FinancialStatementOutputData {
    private String symbol;
    private List<String> statements;

    public FinancialStatementOutputData(String symbol, List<String> statements) {
        this.symbol = symbol;
        this.statements = statements;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<String> getStatements() {
        return statements;
    }
}
