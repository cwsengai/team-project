package interface_adapter.view;

import interface_adapter.view_model.PortfolioViewModel;

/**
 * Interface for portfolio view components.
 * Defines the contract for displaying portfolio data and errors.
 * This abstraction allows the presenter to be decoupled from concrete UI implementations.
 */
public interface PortfolioView {
    /**
     * Render portfolio data in the view.
     * @param viewModel The portfolio view model containing formatted data for display
     */
    void renderPortfolio(PortfolioViewModel viewModel);

    /**
     * Show an error message to the user.
     * @param error The error message to display
     */
    void showError(String error);
}
