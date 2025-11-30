package interfaceadapter.view_model;

import entity.FinancialStatement;
import java.util.List;

public class FinancialStatementViewModel {

    public List<FinancialStatement> statements;
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

