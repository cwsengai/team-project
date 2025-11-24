package interface_adapter.view_model;

import interface_adapter.company_list.CompanyDisplayData;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * View Model for Search Company feature.
 * Holds the state of search results and notifies observers of changes.
 */
public class SearchCompanyViewModel {
    private List<CompanyDisplayData> searchResults = new ArrayList<>();
    private String errorMessage = "";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setSearchResults(List<CompanyDisplayData> results) {
        List<CompanyDisplayData> oldValue = this.searchResults;
        this.searchResults = results;
        support.firePropertyChange("searchResults", oldValue, results);
    }

    public void setErrorMessage(String errorMessage) {
        String oldValue = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("error", oldValue, errorMessage);
    }

    public List<CompanyDisplayData> getSearchResults() {
        return searchResults;
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