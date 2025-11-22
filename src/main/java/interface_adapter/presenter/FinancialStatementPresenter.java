package interface_adapter.presenter;

import java.util.List;

import use_case.financial_statement.FinancialStatementOutputBoundary;
import entity.FinancialStatement;
import interface_adapter.view_model.FinancialStatementViewModel;

public class FinancialStatementPresenter implements FinancialStatementOutputBoundary {

    private final FinancialStatementViewModel vm;

    public FinancialStatementPresenter(FinancialStatementViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void presentFinancialStatement(List<FinancialStatement> list) {
        vm.error = null;
        vm.statements = list;
        vm.formattedOutput = format(list);
        vm.notifyListener();
    }

    @Override
    public void presentError(String message) {
        vm.statements = null;
        vm.formattedOutput = "";
        vm.error = message;
        vm.notifyListener();
    }

    private String format(List<FinancialStatement> list) {
        StringBuilder sb = new StringBuilder();

        for (FinancialStatement fs : list) {
            sb.append("Fiscal Year: ").append(fs.getFiscalDateEnding()).append("\n");
            sb.append("Revenue: ").append(fs.getTotalRevenue()).append("\n");
            sb.append("Gross Profit: ").append(fs.getGrossProfit()).append("\n");
            sb.append("Net Income: ").append(fs.getNetIncome()).append("\n");
            sb.append("Operating Cash Flow: ").append(fs.getOperatingCashFlow()).append("\n");
            sb.append("CapEx: ").append(fs.getCapitalExpenditures()).append("\n");
            sb.append("Dividend Payout: ").append(fs.getDividendPayout()).append("\n");
            sb.append("----------------------------------\n");
        }

        return sb.toString();
    }
}

