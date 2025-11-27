package app;

import javax.swing.SwingUtilities;

import data_access.AlphaVantagePriceGateway;
import framework_and_driver.ChartWindow;
import interface_adapter.controller.IntervalController;
import interface_adapter.presenter.PriceChartPresenter;
import use_case.price_chart.GetPriceByIntervalInteractor;
import use_case.price_chart.PriceChartOutputBoundary;
import use_case.price_chart.PriceDataAccessInterface;

public class TimeIntervalChartMain {
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