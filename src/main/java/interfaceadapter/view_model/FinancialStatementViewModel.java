package interfaceadapter.view_model;

public class FinancialStatementViewModel {

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

    public void setStatements() {
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

