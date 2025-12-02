package usecase.financial_statement;

import java.util.ArrayList;
import java.util.List;

import entity.FinancialStatement;

public record FinancialStatementInteractor(FinancialStatementGateway gateway,
        FinancialStatementOutputBoundary presenter) implements FinancialStatementInputBoundary {

    @Override
    public void execute(FinancialStatementInputData data) {
        final String symbol = data.symbol();
        final List<FinancialStatement> statements = gateway.fetchFinancialStatements(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No financial statements found for: " + symbol);
        } else {
            final List<String> formatted = new ArrayList<>();
            final String constant = "\n";
            for (FinancialStatement fs : statements) {
                final String block = "Fiscal Year: " + fs.fiscalDateEnding() + constant
                        + "Revenue: " + fs.totalRevenue() + constant
                        + "Gross Profit: " + fs.grossProfit() + constant
                        + "Net Income: " + fs.netIncome() + constant
                        + "Operating Cash Flow: " + fs.operatingCashFlow() + constant
                        + "CapEx: " + fs.capitalExpenditures() + constant
                        + "Dividend Payout: " + fs.dividendPayout() + constant
                        + "----------------------------------";
                formatted.add(block);
            }

            final FinancialStatementOutputData output = new FinancialStatementOutputData(symbol, formatted);
            presenter.presentFinancialStatement(output);

        }
    }
}
