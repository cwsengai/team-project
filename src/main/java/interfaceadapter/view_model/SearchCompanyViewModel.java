package interfaceadapter.view_model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import interfaceadapter.company_list.CompanyDisplayData;

/**
 * View Model for Search Company feature.
 * Holds the state of search results and notifies observers of changes.
 */
public class SearchCompanyViewModel {
    private List<CompanyDisplayData> searchResults = new ArrayList<>();
    private String errorMessage = "";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Sets the search results for this view model.
     *
     * @param results the list of company display data
     */
    public void setSearchResults(List<CompanyDisplayData> results) {
        List<CompanyDisplayData> oldValue = this.searchResults;
        this.searchResults = results;
        support.firePropertyChange("searchResults", oldValue, results);
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

    /**
     * Adds a property change listener to this view model.
     *
     * @param listener the property change listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}
