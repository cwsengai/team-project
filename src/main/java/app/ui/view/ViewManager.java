package app.ui.view;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import interfaceadapter.view_model.ViewManagerModel;

public class ViewManager implements PropertyChangeListener {
    private final JPanel views;
    private final CardLayout cardLayout;

    public ViewManager(JPanel views, CardLayout cardLayout) {
        this.views = views;
        this.cardLayout = cardLayout;
    }

    /**
     * Register this ViewManager as a listener with the given model.
     * Separating registration from construction prevents the `this` reference
     * from escaping while the object is still being constructed.
     */
    public void register(ViewManagerModel viewManagerModel) {
        viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("app/ui/view")) {
            final String viewModelName = (String) evt.getNewValue();
            cardLayout.show(views, viewModelName);
        }
    }
}
