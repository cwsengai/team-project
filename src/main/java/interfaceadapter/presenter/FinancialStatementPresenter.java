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
        viewmodel.error = null;
        viewmodel.statements = data.getStatements();
        viewmodel.formattedOutput = String.join("\n", data.getStatements());
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.statements = null;
        viewmodel.formattedOutput = "";
        viewmodel.error = message;
        viewmodel.notifyListener();
    }

}
