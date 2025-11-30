package interfaceadapter.controller;

import entity.TimeInterval;
import usecase.price_chart.PriceInputBoundary;

public class IntervalController {

    private final PriceInputBoundary priceInteractor;
    private String currentTicker = "AAPL"; // Default ticker

    public IntervalController(PriceInputBoundary interactor) {
        this.priceInteractor = interactor;
    }

    public void setCurrentTicker(String ticker) {
        this.currentTicker = ticker;
    }

    public void handleTimeChange(String buttonText) {
        TimeInterval interval = null;

        switch (buttonText) {
            case "5M":
            case "5min":
                interval = TimeInterval.FIVE_MINUTES;
                break;
            case "1D":
            case "1 day":
                interval = TimeInterval.DAILY;
                break;
            case "1W":
            case "1 week":
                interval = TimeInterval.WEEKLY;
                break;
            default:
                System.err.println("ERROR: Unsupported time interval button: " + buttonText);
                break;
        }

        if (interval != null && currentTicker != null) {
            System.out.println("INFO: Requesting price history for " + currentTicker + " with interval " + interval);
            this.priceInteractor.loadPriceHistory(currentTicker, interval);
        }
    }
}