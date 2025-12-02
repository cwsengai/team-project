package usecase.financial_statement;

import java.util.List;

public record FinancialStatementOutputData(String symbol, List<String> statements) {
}

