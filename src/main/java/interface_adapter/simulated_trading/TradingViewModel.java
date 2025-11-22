package interface_adapter.simulated_trading;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TradingViewModel {

    public static final String TITLE_LABEL = "Simulated Trading System";
    public static final String BUY_BUTTON_LABEL = "Buy/Long";
    public static final String SELL_BUTTON_LABEL = "Sell/Short";
    public static final String AMOUNT_LABEL = "Amount(USD)";

    private TradingState state = new TradingState();

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setState(TradingState state) {
        this.state = state;
    }

    public TradingState getState() {
        return state;
    }

    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}