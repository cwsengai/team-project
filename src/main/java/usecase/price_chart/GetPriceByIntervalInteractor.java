package usecase.price_chart;

import java.util.List;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Interactor for loading price history based on a given time interval.
 * Now acts as pure business logic.
 */
public class GetPriceByIntervalInteractor implements PriceInputBoundary {

    private final PriceDataAccessInterface priceGateway;
    private final PriceChartOutputBoundary pricePresenter;

    public GetPriceByIntervalInteractor(PriceDataAccessInterface priceGateway,
                                        PriceChartOutputBoundary pricePresenter) {
        this.priceGateway = priceGateway;
        this.pricePresenter = pricePresenter;
    }

    @Override
    @SuppressWarnings({"checkstyle:IllegalCatch", "checkstyle:SuppressWarnings"})
    public void loadPriceHistory(String ticker, TimeInterval interval) {
        try {
            final List<PricePoint> priceData = priceGateway.getPriceHistory(ticker, interval);

            if (priceData == null || priceData.isEmpty()) {
                pricePresenter.presentError("No " + interval.name() + " price data found.");
            }
            else {
                pricePresenter.presentPriceHistory(priceData, ticker, interval);
            }
        }
        catch (Exception ex) {
            pricePresenter.presentError("Fail to retrieve " + interval.name() + " price: " + ex.getMessage());
        }
    }
}

