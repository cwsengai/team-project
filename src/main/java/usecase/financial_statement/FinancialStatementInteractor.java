package usecase.financial_statement;

import java.util.ArrayList;
import java.util.List;

import entity.FinancialStatement;

public class FinancialStatementInteractor implements FinancialStatementInputBoundary {
    private final FinancialStatementGateway gateway;
    private final FinancialStatementOutputBoundary presenter;

    public FinancialStatementInteractor(FinancialStatementGateway gateway,
                                        FinancialStatementOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(FinancialStatementInputData data) {
        final String symbol = data.getSymbol();
        final List<FinancialStatement> statements = gateway.fetchFinancialStatements(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No financial statements found for: " + symbol);
        }
        else {
            final List<String> formatted = new ArrayList<>();
            final String constant = "\n";
            for (FinancialStatement fs : statements) {
                final String block =
                        "Fiscal Year: " + fs.getFiscalDateEnding() + constant
                                + "Revenue: " + fs.getTotalRevenue() + constant
                                + "Gross Profit: " + fs.getGrossProfit() + constant
                                + "Net Income: " + fs.getNetIncome() + constant
                                + "Operating Cash Flow: " + fs.getOperatingCashFlow() + constant
                                + "CapEx: " + fs.getCapitalExpenditures() + constant
                                + "Dividend Payout: " + fs.getDividendPayout() + constant
                                + "----------------------------------";
                formatted.add(block);
            }

            final FinancialStatementOutputData output = new FinancialStatementOutputData(symbol, formatted);
            presenter.presentFinancialStatement(output);

        }
    }
}
