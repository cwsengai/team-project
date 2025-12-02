package interfaceadapter.presenter;

import interfaceadapter.view_model.FinancialStatementViewModel;
import usecase.financial_statement.FinancialStatementOutputBoundary;
import usecase.financial_statement.FinancialStatementOutputData;

public record FinancialStatementPresenter(
        FinancialStatementViewModel viewmodel) implements FinancialStatementOutputBoundary {

    @Override
    public void presentFinancialStatement(FinancialStatementOutputData data) {
        viewmodel.setError(null);
        viewmodel.setFormattedOutput(String.join("\n", data.statements()));
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.setFormattedOutput("");
        viewmodel.setError(message);
        viewmodel.notifyListener();
    }

}
