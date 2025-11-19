package app;

import java.time.LocalDateTime;

import javax.swing.SwingUtilities;

import data_access.client.SupabaseClient;
import data_access.config.EnvConfig;
import data_access.gateway.StockDataGateway;
import data_access.gateway.alphavantage.AlphaVantageGateway;
import data_access.repository.PortfolioRepository;
import data_access.repository.supabase.SupabasePortfolioRepository;
import entity.Portfolio;
import entity.Position;
import entity.Trade;
import interface_adapter.controller.TradingController;
import interface_adapter.presenter.PortfolioPresenter;
import use_case.track_portfolio.TrackPortfolioInteractor;
import view.MainFrame;
import view.PortfolioPage;

/**
 * The Main class is the entry point of the application.
 * Sets up dependency injection and wires all components together.
 */
public class OrderHistoryView {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            // Initialize the application with Clean Architecture components
            MainFrame frame = createApplication();
            frame.setVisible(true);
        });
    }

    /**
     * Create and wire up all application components.
     * This is where dependency injection happens following the Dependency Inversion Principle.
     * All components depend on abstractions, not concretions.
     */
    private static MainFrame createApplication() {
        // TODO: Replace hardcoded values with configuration
        String portfolioId = "portfolio-001";
        String userId = "user-001";

        // === Data Access Layer ===
        // Initialize Supabase client (uses environment config)
        SupabaseClient supabaseClient = new SupabaseClient();
        
        // Create repository (Supabase)
        PortfolioRepository portfolioRepository = new SupabasePortfolioRepository(supabaseClient);
        
        // Create stock data gateway
        // TODO: Replace with actual API key from configuration
        String apiKey = EnvConfig.getAlphaVantageApiKey();
        if (apiKey.isEmpty()) {
            apiKey = "DEMO_API_KEY";
        }
        StockDataGateway stockDataGateway = new AlphaVantageGateway(apiKey);

        // === Create sample portfolio for demo ===
        // Note: Commented out for Supabase - requires authenticated user
        // createSamplePortfolio(portfolioRepository, portfolioId, userId);

        // === Create main frame ===
        MainFrame frame = new MainFrame();
        
        // === Wire dependencies using interfaces (following DIP) ===
        // Create view (concrete implementation but will be used through interface)
        PortfolioPage portfolioPage = new PortfolioPage(null, portfolioId, userId);
        
        // Create presenter (depends on PortfolioView interface, not concrete PortfolioPage)
        PortfolioPresenter presenter = new PortfolioPresenter(portfolioPage);
        
        // Create interactor (depends on interfaces)
        TrackPortfolioInteractor interactor = new TrackPortfolioInteractor(
                portfolioRepository, stockDataGateway, presenter);
        
        // Create controller (depends on use case interface)
        TradingController controller = new TradingController(interactor);
        
        // Set the controller on the view
        portfolioPage.setController(controller);
        
        // Add portfolio page to frame
        frame.setContentPane(portfolioPage);

        // Load initial portfolio data
        controller.viewPortfolio(portfolioId, userId);

        return frame;
    }

    /**
     * Create a sample portfolio with some positions for demonstration.
     * TODO: Remove this and implement proper portfolio creation UI
     */
    private static void createSamplePortfolio(PortfolioRepository repository, 
                                              String portfolioId, String userId) {
        Portfolio portfolio = new Portfolio(portfolioId, userId, 10000.0);

        // Add sample positions
        // AAPL position
        Position aaplPosition = new Position("AAPL");
        aaplPosition.addTrade(new Trade("T001", "AAPL", 10, 150.0, 
                LocalDateTime.now().minusDays(30), true));
        portfolio.addPosition(aaplPosition);

        // GOOGL position
        Position googlPosition = new Position("GOOGL");
        googlPosition.addTrade(new Trade("T002", "GOOGL", 5, 130.0, 
                LocalDateTime.now().minusDays(20), true));
        portfolio.addPosition(googlPosition);

        // MSFT position
        Position msftPosition = new Position("MSFT");
        msftPosition.addTrade(new Trade("T003", "MSFT", 8, 350.0, 
                LocalDateTime.now().minusDays(15), true));
        portfolio.addPosition(msftPosition);

        // Save to repository
        repository.save(portfolio);
    }

}
