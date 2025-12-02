package frameworkanddriver;

import javax.swing.JFrame;

import entity.CompanyDetailViewModel;
import interfaceadapter.controller.IntervalController;

/**
 * Company detail page UI component that displays stock chart.
 */
public class CompanyDetailPage extends JFrame {

    private ChartPanel chartPanel;
    private IntervalController chartController;
    private String currentTicker;

    /**
     * Updates the company details section of the UI using the data provided
     * by the given {@link CompanyDetailViewModel}. If the supplied view model
     * is {@code null}, no update is performed.
     *
     * @param viewModel the view model containing the latest company detail data
     */
    public void updateCompanyDetails(CompanyDetailViewModel viewModel) {
        if (viewModel == null) {
            return;
        }

        // Store ticker for chart updates
        this.currentTicker = viewModel.getTicker();
        
        // Update chart controller with new ticker
        if (chartController != null) {
            chartController.setCurrentTicker(this.currentTicker);
        }
        
        // Tell ChartPanel the current stock ticker, otherwise it won't know which stock to zoom
        if (chartPanel != null) {
            chartPanel.enableZoom(this.currentTicker);
        }
    }

    /**
     * Updates the chart display using the provided {@link entity.ChartViewModel}.
     * If the chart panel has not been initialized, the update request is ignored.
     *
     * @param chartViewModel the view model containing the latest chart data
     */
    public void updateChart(entity.ChartViewModel chartViewModel) {
        if (chartPanel != null) {
            chartPanel.updateChart(chartViewModel);
        }
    }

    /**
     * Displays an error message to the user in a modal dialog. This method uses
     * a standard Swing {@link javax.swing.JOptionPane} to show the message with
     * an error icon.
     *
     * @param message the error message to display
     */
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns the underlying {@link ChartPanel} used by this view component.
     * This allows presenters or controllers to send error messages or chart updates
     * directly to the panel when needed.
     *
     * @return the chart panel associated with this view
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
