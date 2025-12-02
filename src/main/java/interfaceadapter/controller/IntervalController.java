package interfaceadapter.controller;

import entity.TimeInterval;
import usecase.price_chart.PriceInputBoundary;

public class IntervalController {

    private final PriceInputBoundary priceInteractor;
    private String currentTicker = "AAPL";
    // Default ticker

    public IntervalController(PriceInputBoundary interactor) {
        this.priceInteractor = interactor;
    }

    public void setCurrentTicker(String ticker) {
        this.currentTicker = ticker;
    }

    /**
     * Handles time interval change requests from the UI.
     *
     * @param buttonText the button text indicating the desired time interval
     */
    public void handleTimeChange(String buttonText) {
        TimeInterval interval = null;

        switch (buttonText) {
            case "5M", "5min" -> interval = TimeInterval.FIVE_MINUTES;
            case "1D", "1 day" -> interval = TimeInterval.DAILY;
            case "1W", "1 week" -> interval = TimeInterval.WEEKLY;
            default -> System.err.println("ERROR: Unsupported time interval button: " + buttonText);
        }

        if (interval != null && currentTicker != null) {
            System.out.println("INFO: Requesting price history for " + currentTicker + " with interval " + interval);
            this.priceInteractor.loadPriceHistory(currentTicker, interval);
        }
    }
}
