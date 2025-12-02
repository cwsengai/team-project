package interfaceadapter.view_model;

public class CompanyViewModel {

    private String name;
    private String symbol;
    private String sector;
    private String industry;
    private String description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
