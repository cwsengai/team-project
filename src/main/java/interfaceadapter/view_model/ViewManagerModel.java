package interfaceadapter.view_model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {
    private String activeView;

    // ViewManager (Main class) listens to this model
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getActiveView() {
        return activeView;
    }

    public void setActiveView(String activeView) {
        this.activeView = activeView;
    }

    // Notify listeners (ViewManager) when the active view changes
    /**
     * Fires a property change event when the active view changes.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("app/ui/view", null, this.activeView);
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
