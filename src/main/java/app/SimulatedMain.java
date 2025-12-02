package app;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import app.ui.view.SetupView;
import app.ui.view.TradingView;
import app.ui.view.ViewManager;
import dataaccess.AlphaVantagePriceGateway;
import dataaccess.InMemorySessionDataAccessObject;
import dataaccess.SimulationMarketDataAccess;
import dataaccess.SupabasePortfolioDataAccessObject;
import dataaccess.SupabaseTradeDataAccessObject;
import entity.Account;
import interfaceadapter.setup_simulation.SetupController;
import interfaceadapter.setup_simulation.SetupPresenter;
import interfaceadapter.setup_simulation.SetupViewModel;
import interfaceadapter.simulated_trading.TradingController;
import interfaceadapter.simulated_trading.TradingPresenter;
import interfaceadapter.simulated_trading.TradingViewModel;
import interfaceadapter.view_model.ViewManagerModel;
import usecase.price_chart.PriceDataAccessInterface;
import usecase.setup_simulation.SetupInputData;
import usecase.setup_simulation.SetupInteractor;
import usecase.simulated_trade.SimulatedTradeInteractor;
import usecase.simulated_trade.SimulationDataAccessInterface;
import usecase.update_market.UpdateMarketInteractor;

public class SimulatedMain {

    private static final PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
    private static final SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);
    private static Optional<SetupInputData> setupInput = Optional.empty();

    /**
     * Listener responsible for creating and switching to the TradingView once
     * the setup process has completed. Acts as a bridge between the SetupViewModel
     * and the TradingViewModel, constructing the TradingView when the appropriate
     * property change event is fired.
     */
    private static class TradingViewFactoryListener implements PropertyChangeListener {

        private final JPanel views;
        private final CardLayout cardLayout;
        private final TradingViewModel tradingViewModel;
        private final ViewManagerModel viewManagerModel;

        public TradingViewFactoryListener(JPanel views, CardLayout cardLayout,
                                          TradingViewModel tradingViewModel,
                                          ViewManagerModel viewManagerModel) {
            this.views = views;
            this.cardLayout = cardLayout;
            this.tradingViewModel = tradingViewModel;
            this.viewManagerModel = viewManagerModel;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (viewManagerModel.getActiveView().equals(TradingViewModel.VIEW_NAME) && setupInput.isPresent()) {
                final SetupInputData input = setupInput.get();
                final String ticker = input.getTicker();

                // ---------------------------------------------------------------------
                // 1. REAL LOGIN (your login page) — REPLACED ONLY THIS PART
                // ---------------------------------------------------------------------
                final InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();

                final app.ui.LoginPage loginWindow = new app.ui.LoginPage(null, sessionDAO);
                loginWindow.setVisible(true);

                if (!loginWindow.wasSuccessful()) {
                    throw new RuntimeException("User cancelled login.");
                }

                final SupabaseTradeDataAccessObject tradeDAO = new SupabaseTradeDataAccessObject();

                final String userId = sessionDAO.getCurrentUserId().toString();
                // ---------------------------------------------------------------------

                // --- 2. Core Entity Setup ---
                final Account account = new Account(input.getInitialBalance(), userId);

                // Save portfolio to database
                final SupabasePortfolioDataAccessObject portfolioDAO = new SupabasePortfolioDataAccessObject();
                portfolioDAO.savePortfolio(UUID.fromString(userId), input.getInitialBalance());

                account.addTradeClosedListener(record -> {
                    try {
                        final UUID userUuid = UUID.fromString(record.getUserId());
                        tradeDAO.saveTrade(record, userUuid);
                    }
                    catch (Exception ex) {
                        System.err.println("DB Save Failed: " + ex.getMessage());
                    }
                });

                // --- 3. Clean Architecture Assembly ---

                final TradingPresenter tradingPresenter = new TradingPresenter(
                        tradingViewModel
                );

                final UpdateMarketInteractor updateMarketInteractor = new UpdateMarketInteractor(
                        simulationDAO, tradingPresenter, account, ticker
                );
                updateMarketInteractor.setSpeed(input.getSpeedMultiplier());

                final SimulatedTradeInteractor tradeInteractor = new SimulatedTradeInteractor(
                        tradingPresenter, account
                );

                final TradingController tradingController = new TradingController(
                        updateMarketInteractor,
                        tradeInteractor,
                        sessionDAO
                );

                // --- 4. View Creation ---
                views.removeAll();

                final TradingView tradingView = new TradingView(tradingController, tradingViewModel);
                views.add(tradingView, TradingViewModel.VIEW_NAME);

                cardLayout.show(views, TradingViewModel.VIEW_NAME);
                views.revalidate();
                views.repaint();

                // --- 5. Start Engine ---
                new Thread(() -> {
                    System.out.println("Loading data for " + ticker + "...");
                    updateMarketInteractor.loadData(ticker);
                }).start();

                setupInput = Optional.empty();
            }
        }
    }

    // Final Setup Presenter
    public static class FinalSetupPresenter extends SetupPresenter {
        public FinalSetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel,
                                   SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        @Override
        public void prepareSuccessView(SetupInputData input) {
            SimulatedMain.setupInput = Optional.of(input);
            super.prepareSuccessView(input);
        }
    }

    // Main Entry Point
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

        String preloadedSymbol = null;

        if (args != null && args.length > 0) {
            preloadedSymbol = args[0];
            System.out.println("Starting CompanyPage with preloaded symbol: " + preloadedSymbol);
        }

        final String symbolForLambda = preloadedSymbol;

        final JFrame application = new JFrame(TradingViewModel.TITLE_LABEL);
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final CardLayout cardLayout = new CardLayout();
        final JPanel views = new JPanel(cardLayout);
        application.add(views);

        // Models
        final ViewManagerModel viewManagerModel = new ViewManagerModel();
        final TradingViewModel tradingViewModel = new TradingViewModel();
        final SetupViewModel setupViewModel = new SetupViewModel();

        // Setup Assembly
        final SetupPresenter finalSetupPresenter = new FinalSetupPresenter(
                viewManagerModel, tradingViewModel, setupViewModel
        );
        final SetupInteractor setupInteractor = new SetupInteractor(
                finalSetupPresenter
        );
        final SetupController setupController = new SetupController(setupInteractor);

        // Init Views
        final SetupView setupView = new SetupView(setupController, setupViewModel);
        final TradingView tradingPlaceholder = new TradingView(null, tradingViewModel);

        views.add(setupView, SetupViewModel.VIEW_NAME);
        views.add(tradingPlaceholder, TradingViewModel.VIEW_NAME);

        viewManagerModel.addPropertyChangeListener(new TradingViewFactoryListener(
                views, cardLayout, tradingViewModel, viewManagerModel
        ));

        new ViewManager(views, cardLayout, viewManagerModel);

        // Launch
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setSize(1300, 750);

        if (symbolForLambda != null) {
            setupView.setInitialSymbol(symbolForLambda);
        }

        application.setVisible(true);
    }
}
