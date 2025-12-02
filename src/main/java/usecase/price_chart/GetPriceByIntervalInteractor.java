package usecase.price_chart;

import java.util.List;

import javax.swing.SwingUtilities;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Interactor for loading price history based on a given time interval.
 * 
 * @param priceGateway   the gateway used to fetch price history
 * @param pricePresenter the presenter that formats and outputs results
 */
public record GetPriceByIntervalInteractor(PriceDataAccessInterface priceGateway,
        PriceChartOutputBoundary pricePresenter) implements PriceInputBoundary {

    /**
     * Creates the interactor responsible for retrieving and presenting price data.
     *
     * @param priceGateway   the gateway used to fetch price history
     * @param pricePresenter the presenter that formats and outputs results
     */
    public GetPriceByIntervalInteractor {
    }

    /**
     * Loads price history for a given ticker and interval.
     *
     * @param ticker   the stock ticker symbol
     * @param interval the selected interval range
     */
    @Override
    public void loadPriceHistory(String ticker, TimeInterval interval) {

        Thread backgroundThread = new Thread(() -> {

            List<PricePoint> priceData;

            try {
                priceData = priceGateway.getPriceHistory(ticker, interval);
            } catch (Exception ex) {

                SwingUtilities.invokeLater(() -> pricePresenter.presentError(
                        "Fail to retrieve " + interval.name() + " price: " + ex.getMessage()));

                return;
            }

            SwingUtilities.invokeLater(() -> {
                if (priceData == null || priceData.isEmpty()) {
                    pricePresenter.presentError(
                            "No " + interval.name() + " price data found (not found).");
                } else {
                    pricePresenter.presentPriceHistory(priceData, ticker, interval);
                }
            });
        });

        backgroundThread.start();
    }
}
