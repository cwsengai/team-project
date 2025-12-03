package usecase.financial_statement;

public class FinancialStatementInputData {
    private final String symbol;

    public FinancialStatementInputData(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
