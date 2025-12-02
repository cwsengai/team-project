package app;

import javax.swing.SwingUtilities;

import dataaccess.AlphaVantagePriceGateway;
import frameworkanddriver.ChartWindow;
import interfaceadapter.controller.IntervalController;
import interfaceadapter.presenter.PriceChartPresenter;
import usecase.price_chart.GetPriceByIntervalInteractor;
import usecase.price_chart.PriceChartOutputBoundary;
import usecase.price_chart.PriceDataAccessInterface;

public class TimeIntervalChartMain {
    /**
     * Entry point for the UC4 Price Chart module. Initializes all components of the
     * price chart feature—including data access, presenter, interactor, controller,
     * and the chart window—and then launches the GUI on the Swing event-dispatch thread.
     *
     * <p>This method performs all required wiring for the Clean Architecture layers:
     * the data gateway, presenter, interactor, and controller are connected before
     * the chart window is displayed.</p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("--- Starting UC4 Price Chart Module (GUI) ---");

        // 1. GATEWAY (Implementation of Data Access Port)
        final PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();

        // 2. VIEW (Frameworks & Drivers)
        final ChartWindow chartWindow = new ChartWindow();

        // 3. PRESENTATION (Interface Adapter)
        final PriceChartOutputBoundary pricePresenter = new PriceChartPresenter(chartWindow);

        // 4. INTERACTOR (Use Case)
        final GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(
                priceGateway,
                pricePresenter
        );

        // 5. CONTROLLER (Interface Adapter)
        final IntervalController intervalController = new IntervalController(interactor);

        // 6. WIRING (Connecting the pieces)
        chartWindow.setController(intervalController);
        SwingUtilities.invokeLater(() -> chartWindow.setVisible(true));

        System.out.println("GUI Initialization Complete.");
    }
}
