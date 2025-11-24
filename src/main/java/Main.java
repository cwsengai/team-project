import javax.swing.SwingUtilities;

import api.AlphaVantagePriceGateway;
import framework_and_driver.ChartWindow;
import interface_adapter.IntervalController;
import interface_adapter.PriceChartPresenter;
import use_case.GetPriceByIntervalInteractor;
import use_case.PriceChartOutputBoundary;
import use_case.PriceDataAccessInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting UC4 Price Chart Module (GUI) ---");

        // 1. GATEWAY (Implementation of Data Access Port)
        PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();

        // 2. VIEW (Frameworks & Drivers)
        ChartWindow chartWindow = new ChartWindow();

        // 3. PRESENTATION (Interface Adapter)
        PriceChartOutputBoundary pricePresenter = new PriceChartPresenter(chartWindow);

        // 4. INTERACTOR (Use Case)
        GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(
                priceGateway,
                pricePresenter
        );

        // 5. CONTROLLER (Interface Adapter)
        IntervalController intervalController = new IntervalController(interactor);


        // 6. WIRING (Connecting the pieces)
        chartWindow.setController(intervalController);

        SwingUtilities.invokeLater(() -> {
            chartWindow.setVisible(true);
        });

        System.out.println("GUI Initialization Complete.");
    }
}