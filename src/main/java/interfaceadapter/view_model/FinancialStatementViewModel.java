package interfaceadapter.view_model;

import java.util.List;

public class FinancialStatementViewModel {

    public List<String> statements;
    public String formattedOutput;
    public String error;

    private Runnable listener;

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public void notifyListener() {
        if (listener != null) listener.run();
    }
}

