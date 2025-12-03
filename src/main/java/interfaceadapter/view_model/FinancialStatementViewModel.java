package interfaceadapter.view_model;

import java.util.List;

public class FinancialStatementViewModel {

    private List<String> statements;
    private String formattedOutput;
    private String error;

    private Runnable listener;

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    /**
     * Notifies the listener of property changes.
     */
    public void notifyListener() {
        if (listener != null) {
            listener.run();
        }
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public String getFormattedOutput() {
        return formattedOutput;
    }

    public void setFormattedOutput(String formattedOutput) {
        this.formattedOutput = formattedOutput;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

