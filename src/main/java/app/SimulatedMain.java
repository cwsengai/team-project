package app;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;
import java.util.UUID; // Needed for ID conversion

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

// 1. Imports for Teammate's API and Data Access
import api.AlphaVantagePriceGateway;
import data_access.InMemorySessionDataAccessObject;
import data_access.SimulationMarketDataAccess;
import data_access.SupabaseTradeDataAccessObject;

// 2. Imports for Entities and Interfaces
import entity.Account;
import entity.SimulatedTradeRecord;
import use_case.simulated_trade.TradeClosedListener;

// 3. Imports for Adapters
import interface_adapter.ViewManagerModel;
import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupPresenter;
import interface_adapter.setup_simulation.SetupViewModel;
import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;

// 4. Imports for Use Cases
import use_case.PriceDataAccessInterface;
import use_case.setup_simulation.SetupInputData;
import use_case.setup_simulation.SetupInteractor;
import use_case.simulated_trade.SimulatedTradeInteractor;
import use_case.simulated_trade.SimulationDataAccessInterface;
import use_case.update_market.UpdateMarketInteractor;

// 5. Imports for Views
import view.SetupView;
import view.TradingView;
import view.ViewManager;

public class SimulatedMain {

    // Initialize base data source (API Gateway -> Simulation Adapter)
    private static final PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
    private static final SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);

    // Global state to temporarily hold Setup data
    private static Optional<SetupInputData> setupInput = Optional.empty();

    /**
     * Factory Listener: Responsible for dynamically creating the Trading components
     * when the view switches from "Setup" to "Trading".
     */
    private static class TradingViewFactoryListener implements PropertyChangeListener {

        private final JPanel views;
        private final CardLayout cardLayout;
        private final TradingViewModel tradingViewModel;
        private final ViewManagerModel viewManagerModel;

        public TradingViewFactoryListener(JPanel views, CardLayout cardLayout, TradingViewModel tradingViewModel,
                                          ViewManagerModel viewManagerModel) {
            this.views = views;
            this.cardLayout = cardLayout;
            this.tradingViewModel = tradingViewModel;
            this.viewManagerModel = viewManagerModel;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Only execute when switching to "trading" view and input data is ready
            if (viewManagerModel.getActiveView().equals(TradingViewModel.VIEW_NAME) && setupInput.isPresent()) {
                SetupInputData input = setupInput.get();
                String ticker = input.getTicker();

                // --- A. Initialize Teammate's Environment (Session & Database) ---
                InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();
                SupabaseTradeDataAccessObject tradeDAO = new SupabaseTradeDataAccessObject();
                try {
                    // Auto-login a random user to get a valid session/ID
                    util.SupabaseRandomUserUtil.createAndLoginRandomUser(sessionDAO);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create random user for session", e);
                }

                // --- B. Get User ID ---
                // Convert UUID to String for the Account entity
                String userId = sessionDAO.getCurrentUserId().toString();

                // --- C. Create Core Entity: Account ---
                Account account = new Account(input.getInitialBalance(), userId);

                // --- D. Connect Database via Observer Pattern ---
                // Register a listener to the Account. When a trade closes, this code runs.
                account.addTradeClosedListener(new TradeClosedListener() {
                    @Override
                    public void onTradeClosed(SimulatedTradeRecord record) {
                        System.out.println(">> Observer Triggered: Saving trade to Supabase...");
                        try {
                            // Convert String ID back to UUID for teammate's DAO
                            UUID userUuid = UUID.fromString(record.getUserId());

                            // Call teammate's save method
                            tradeDAO.saveTrade(record, userUuid);
                        } catch (Exception e) {
                            System.err.println("Failed to save trade: " + e.getMessage());
                        }
                    }
                });

                // --- E. Assemble Clean Architecture Components ---
                TradingPresenter tradingPresenter = new TradingPresenter(tradingViewModel);

                // 1. Market Engine Interactor
                UpdateMarketInteractor updateMarketInteractor = new UpdateMarketInteractor(
                        simulationDAO, tradingPresenter, account, ticker
                );
                updateMarketInteractor.setSpeed(input.getSpeedMultiplier());

                // 2. Trade Execution Interactor
                // (Clean constructor: no DAO passed here, handled by Observer above)
                SimulatedTradeInteractor tradeInteractor = new SimulatedTradeInteractor(
                        tradingPresenter, account
                );

                // 3. Controller
                TradingController tradingController = new TradingController(updateMarketInteractor, tradeInteractor);

                // --- F. View Switching ---
                views.removeAll(); // Clear old views

                TradingView tradingView = new TradingView(tradingController, tradingViewModel);
                views.add(tradingView, TradingViewModel.VIEW_NAME);

                cardLayout.show(views, TradingViewModel.VIEW_NAME);
                views.revalidate();
                views.repaint();

                // --- G. Start Engine (Async Data Loading) ---
                new Thread(() -> {
                    System.out.println("Starting asynchronous data loading for " + ticker + "...");
                    updateMarketInteractor.loadData(ticker);
                }).start();

                // Clear setup data
                setupInput = Optional.empty();
            }
        }
    }

    // --- Final Setup Presenter (Intercepts input data) ---
    public static class FinalSetupPresenter extends SetupPresenter {
        public FinalSetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        @Override
        public void prepareSuccessView(SetupInputData input) {
            // Capture data globally
            SimulatedMain.setupInput = Optional.of(input);
            // Proceed with view switch
            super.prepareSuccessView(input);
        }
    }

    // --- Main Entry Point ---
    public static void main(String[] args) {
        JFrame application = new JFrame(TradingViewModel.TITLE_LABEL);
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        // Models
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        TradingViewModel tradingViewModel = new TradingViewModel();
        SetupViewModel setupViewModel = new SetupViewModel();

        // Setup Components
        SetupPresenter finalSetupPresenter = new FinalSetupPresenter(
                viewManagerModel, tradingViewModel, setupViewModel
        );
        SetupInteractor setupInteractor = new SetupInteractor(
                finalSetupPresenter, simulationDAO
        );
        SetupController setupController = new SetupController(setupInteractor);

        // Views
        SetupView setupView = new SetupView(setupController, setupViewModel);
        view.TradingView tradingPlaceholder = new view.TradingView(null, tradingViewModel);

        views.add(setupView, SetupViewModel.VIEW_NAME);
        views.add(tradingPlaceholder, TradingViewModel.VIEW_NAME);

        // Listeners
        viewManagerModel.addPropertyChangeListener(new TradingViewFactoryListener(
                views, cardLayout, tradingViewModel, viewManagerModel
        ));

        new ViewManager(views, cardLayout, viewManagerModel);

        // Start Application
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setSize(1300, 750);
        application.setVisible(true);
    }
}