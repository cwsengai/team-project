package interface_adapter.view_model;

public class CompanyViewModel {

    public String name;
    public String symbol;
    public String sector;
    public String industry;
    public String description;
    public String error;

    private Runnable listener;

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public void notifyListener() {
        if (listener != null) listener.run();
    }
}
