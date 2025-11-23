package interface_adapter.controller.simulated_trading;

import java.time.LocalDateTime;

import use_case.simulated_trade.SimulatedTradeInteractor;

public class SimulatedTradeController {

    private final SimulatedTradeInteractor interactor;

    public SimulatedTradeController(SimulatedTradeInteractor interactor) {
        this.interactor = interactor;
    }

    public void saveTrade(String ticker, boolean isLong, int quantity, double entry, double exit) {
        // Validate basics here if you want, or let Interactor do it
        LocalDateTime now = LocalDateTime.now();
        
        // Assume entry was sometime in the past for this simulation
        LocalDateTime entryTime = now.minusMinutes(15); 
        
        interactor.execute(ticker, isLong, quantity, entry, exit, entryTime, now);
    }
}