package interfaceadapter.view_model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import interfaceadapter.company_list.CompanyDisplayData;

public class CompanyListViewModel {
    private List<CompanyDisplayData> companies = new ArrayList<>();
    private String errorMessage = "";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Sets the list of companies to display.
     *
     * @param companies the list of company display data
     */
    public void setCompanies(List<CompanyDisplayData> companies) {
        List<CompanyDisplayData> oldValue = this.companies;
        this.companies = companies;
        support.firePropertyChange("companies", oldValue, companies);
    }

    /**
     * Sets the error message for this view model.
     *
     * @param errorMessage the error message to set
     */
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

    /**
     * Adds a property change listener to this view model.
     *
     * @param listener the property change listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from this view model.
     *
     * @param listener the property change listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
