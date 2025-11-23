package use_case;

import entity.PricePoint;
import entity.TimeInterval;
import java.util.List;
import javax.swing.SwingUtilities;

public class GetPriceByIntervalInteractor implements PriceInputBoundary {

    private final PriceDataAccessInterface priceGateway;
    private final PriceChartOutputBoundary pricePresenter;

    public GetPriceByIntervalInteractor(PriceDataAccessInterface priceGateway,
                                        PriceChartOutputBoundary pricePresenter) {
        this.priceGateway = priceGateway;
        this.pricePresenter = pricePresenter;
    }

    @Override
    public void loadPriceHistory(String ticker, TimeInterval interval) {
        // Run API call in background thread to prevent UI blocking
        new Thread(() -> {
            List<PricePoint> priceData;

            try {
                // call Gateway attain data (interface) - this may take time
                priceData = priceGateway.getPriceHistory(ticker, interval);
            } catch (Exception e) {
                // report error on EDT
                SwingUtilities.invokeLater(() -> {
                    pricePresenter.presentError("fail to attain " + interval.name() + " price: " + e.getMessage());
                });
                return;
            }

            // Update UI on EDT (Event Dispatch Thread)
            SwingUtilities.invokeLater(() -> {
                if (priceData == null || priceData.isEmpty()) {
                    pricePresenter.presentError("not found " + interval.name() + " data price");
                } else {
                    // send to presenter
                    pricePresenter.presentPriceHistory(priceData, ticker, interval);
                }
            });
        }).start();
    }
}