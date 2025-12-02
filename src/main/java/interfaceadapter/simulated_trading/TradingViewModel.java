package interfaceadapter.simulated_trading;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Represents the ViewModel for the trading screen, storing UI text labels
 * and managing the state object for the trading view.
 */
public class TradingViewModel {

    /** The view name used by the ViewManager. */
    public static final String VIEW_NAME = "trading";

    /** The window title label. */
    public static final String TITLE_LABEL = "Simulated Trading System";

    /** The label for the buy/long button. */
    public static final String BUY_BUTTON_LABEL = "Buy/Long";

    /** The label for the sell/short button. */
    public static final String SELL_BUTTON_LABEL = "Sell/Short";

    /** The current state displayed by this ViewModel. */
    private TradingState state = new TradingState();

    /** Manages property change listeners for the view. */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Updates the stored state for this ViewModel.
     *
     * @param state the new trading state
     */
    public void setState(TradingState state) {
        this.state = state;
    }

    /**
     * Returns the current trading state.
     *
     * @return the current state
     */
    public TradingState getState() {
        return state;
    }

    /**
     * Notifies all registered listeners that the state has changed.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    /**
     * Registers a listener to receive property change events.
     *
     * @param listener the listener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
