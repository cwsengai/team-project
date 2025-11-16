package interface_adapter.presenter;

import entity.Position;
import interface_adapter.view.PortfolioView;
import interface_adapter.view_model.PortfolioViewModel;
import interface_adapter.view_model.PositionView;
import use_case.track_portfolio.TrackPortfolioOutputBoundary;
import use_case.track_portfolio.TrackPortfolioOutputData;

/**
 * Presenter for the TrackPortfolio use case.
 * Transforms output data into view model and updates the UI.
 * Depends on PortfolioView abstraction rather than concrete view implementation.
 */
public class PortfolioPresenter implements TrackPortfolioOutputBoundary {
    private final PortfolioView view;

    public PortfolioPresenter(PortfolioView view) {
        this.view = view;
    }

    @Override
    public void presentPortfolio(TrackPortfolioOutputData outputData) {
        // Transform output data to view model
        PortfolioViewModel viewModel = toViewModel(outputData);
        
        // Update view through abstraction
        view.renderPortfolio(viewModel);
    }

    @Override
    public void presentError(String error) {
        view.showError(error);
    }

    /**
     * Convert output data to view model.
     * TODO: Add price lookup for current market prices in positions
     */
    private PortfolioViewModel toViewModel(TrackPortfolioOutputData outputData) {
        // Convert positions to position views
        Position[] positions = outputData.getPositions();
        PositionView[] positionViews = new PositionView[positions.length];
        
        for (int i = 0; i < positions.length; i++) {
            Position position = positions[i];
            // TODO: Get current market price for each position
            double currentPrice = 100.0; // Placeholder - should get from output data
            
            positionViews[i] = new PositionView(
                    position.getTicker(),
                    position.getQuantity(),
                    currentPrice,
                    position.getAverageCost(),
                    position.unrealizedGain(currentPrice)
            );
        }

        return new PortfolioViewModel(
                outputData.getPortfolioId(),
                positionViews,
                outputData.getTotalRealizedGain(),
                outputData.getTotalUnrealizedGain(),
                outputData.getSnapshotTime()
        );
    }

    /**
     * TODO: Add method to format numbers for display
     * TODO: Add method to calculate percentage gains
     * TODO: Add method to sort positions by different criteria
     */
}
