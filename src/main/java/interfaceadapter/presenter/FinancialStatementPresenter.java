package interfaceadapter.presenter;

import interfaceadapter.view_model.FinancialStatementViewModel;
import usecase.financial_statement.FinancialStatementOutputBoundary;
import usecase.financial_statement.FinancialStatementOutputData;

public class FinancialStatementPresenter implements FinancialStatementOutputBoundary {

    private final FinancialStatementViewModel viewmodel;

    public FinancialStatementPresenter(FinancialStatementViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void presentFinancialStatement(FinancialStatementOutputData data) {
        viewmodel.setError(null);
        viewmodel.setStatements(data.getStatements());
        viewmodel.setFormattedOutput(String.join("\n", data.getStatements()));
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.setStatements(null);
        viewmodel.setFormattedOutput("");
        viewmodel.setError(message);
        viewmodel.notifyListener();
    }

}
