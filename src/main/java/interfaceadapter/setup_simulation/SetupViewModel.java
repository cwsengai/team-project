package interfaceadapter.setup_simulation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SetupViewModel {

    // ViewManager uses this name
    public static final String VIEW_NAME = "setup";
    public static final String START_BUTTON_LABEL = "START";

    private String error = null;

    /**
     * Sets the error message for this view model.
     *
     * @param error the error message to set
     */
    public void setError(String error) {
        this.error = error;
        firePropertyChanged();
    }

    public String getError() {
        return error;
    }

    // PropertyChangeSupport implementation
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Fires a property change event for the error property.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("error", null, this.error);
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
