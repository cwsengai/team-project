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
     * Adds a property change listener to this view model.
     *
     * @param listener the property change listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}
