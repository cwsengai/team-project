package app;

import data_access.AlphaVantagePriceGateway;
import framework_and_driver.ChartWindow;
import interface_adapter.controller.IntervalController;
import interface_adapter.presenter.PriceChartPresenter;
import javax.swing.SwingUtilities;
import use_case.price_chart.GetPriceByIntervalInteractor;
import use_case.price_chart.PriceChartOutputBoundary;
import use_case.price_chart.PriceDataAccessInterface;

/**
 * Main entry point for the Time Interval Chart Use Case (UC4) demo.
 */
public class TimeIntervalChartMain {

  /**
   * Main method to launch the chart window independently.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    System.out.println("--- Starting UC4 Price Chart Module (GUI) ---");

    // 1. GATEWAY
    PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();

    // 2. VIEW
    ChartWindow chartWindow = new ChartWindow();

    // 3. PRESENTATION
    PriceChartOutputBoundary pricePresenter = new PriceChartPresenter(chartWindow);

    // 4. INTERACTOR
    GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(
        priceGateway,
        pricePresenter
    );

    // 5. CONTROLLER
    IntervalController intervalController = new IntervalController(interactor);

    // 6. WIRING
    chartWindow.setController(intervalController);

    SwingUtilities.invokeLater(() -> {
      chartWindow.setVisible(true);
    });

    System.out.println("GUI Initialization Complete.");
  }
}