package interface_adapter;

import entity.TimeInterval;
import use_case.PriceInputBoundary;

public class IntervalController {

    private final PriceInputBoundary priceInteractor;

    public IntervalController(PriceInputBoundary interactor) {
        this.priceInteractor = interactor;
    }

    public void handleTimeChange(String buttonText) {
        TimeInterval interval = null;

        switch (buttonText) {
            case "5M":
                interval = TimeInterval.INTRADAY;
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
            String currentTicker = "DOW";
            System.out.println("INFO: Requesting price history for " + currentTicker + " with interval " + interval);
            this.priceInteractor.loadPriceHistory(currentTicker, interval);
        }
    }
}