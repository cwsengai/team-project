package interface_adapter;

import use_case.PriceInputBoundary;
import entity.TimeInterval;

public class IntervalController {

    private final PriceInputBoundary priceInteractor;
    private final String currentTicker = "DOW";

    public IntervalController(PriceInputBoundary interactor) {
        this.priceInteractor = interactor;
    }

    public void handleTimeChange(String buttonText) {
        TimeInterval interval = null;

        switch (buttonText) {
            case "5M":
                interval = TimeInterval.Five_Minutes;
                break;
            case "1D":
                interval = TimeInterval.DAILY;
                break;
            case "1W":
                interval = TimeInterval.WEEKLY;
                break;
            default:
                System.err.println("ERROR: Unsupported time interval button: " + buttonText);
                break;
        }

        if (interval != null) {
            System.out.println("INFO: Requesting price history for " + currentTicker + " with interval " + interval);
            this.priceInteractor.loadPriceHistory(currentTicker, interval);
        }
    }
}