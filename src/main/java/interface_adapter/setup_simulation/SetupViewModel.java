package interface_adapter.setup_simulation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SetupViewModel {

    public static final String VIEW_NAME = "setup"; // ViewManager uses this name
    public static final String TITLE_LABEL = "Simulation Setup";
    public static final String START_BUTTON_LABEL = "START";

    private String error = null;

    public String getViewName() { return VIEW_NAME; }

    public void setError(String error) {
        this.error = error;
        firePropertyChanged();
    }

    public String getError() { return error; }

    // PropertyChangeSupport implementation
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    public void firePropertyChanged() {
        support.firePropertyChange("error", null, this.error);
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}