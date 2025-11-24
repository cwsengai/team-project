package interface_adapter.view_model;

import interface_adapter.company_list.CompanyDisplayData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class CompanyListViewModel {
    private List<CompanyDisplayData> companies = new ArrayList<>();
    private String errorMessage = "";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setCompanies(List<CompanyDisplayData> companies) {
        List<CompanyDisplayData> oldValue = this.companies;
        this.companies = companies;
        support.firePropertyChange("companies", oldValue, companies);
    }

    public void setErrorMessage(String errorMessage) {
        String oldValue = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("error", oldValue, errorMessage);
    }

    public List<CompanyDisplayData> getCompanies() {
        return companies;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
