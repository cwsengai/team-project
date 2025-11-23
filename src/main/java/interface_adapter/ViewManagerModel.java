package interface_adapter;

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
    public void firePropertyChanged() {
        support.firePropertyChange("view", null, this.activeView);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}