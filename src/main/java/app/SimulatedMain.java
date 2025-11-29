package app;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dataaccess.AlphaVantagePriceGateway;
import dataaccess.InMemorySessionDataAccessObject;
import dataaccess.SimulationMarketDataAccess;
import dataaccess.SupabaseTradeDataAccessObject;
import entity.Account;
import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupPresenter;
import interface_adapter.setup_simulation.SetupViewModel;
import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;
import interface_adapter.view_model.ViewManagerModel;
import use_case.price_chart.PriceDataAccessInterface;
import use_case.setup_simulation.SetupInputData;
import use_case.setup_simulation.SetupInteractor;
import use_case.simulated_trade.SimulatedTradeInteractor;
import use_case.simulated_trade.SimulationDataAccessInterface;
import use_case.update_market.UpdateMarketInteractor;
import view.SetupView;
import view.TradingView;
import view.ViewManager;
/**
 * Main entry point for launching the simulated trading system.
 */

public class SimulatedMain {

    private static final int WINDOW_WIDTH = 1300;
    private static final int WINDOW_HEIGHT = 750;

    private static final PriceDataAccessInterface BASE_GATEWAY =
            new AlphaVantagePriceGateway();

    private static final SimulationDataAccessInterface SIMULATION_DAO =
            new SimulationMarketDataAccess(BASE_GATEWAY);

    private static Optional<SetupInputData> setupInput = Optional.empty();

    /**
     * Listens for view changes and constructs TradingView when Setup is completed.
     */
    private static class TradingViewFactoryListener implements PropertyChangeListener {

        private final JPanel views;
        private final CardLayout cardLayout;
        private final TradingViewModel tradingViewModel;
        private final ViewManagerModel viewManagerModel;
        private final SetupViewModel setupViewModel;

        TradingViewFactoryListener(JPanel views,
                                   CardLayout cardLayout,
                                   TradingViewModel tradingViewModel,
                                   ViewManagerModel viewManagerModel,
                                   SetupViewModel setupViewModel) {
            this.views = views;
            this.cardLayout = cardLayout;
            this.tradingViewModel = tradingViewModel;
            this.viewManagerModel = viewManagerModel;
            this.setupViewModel = setupViewModel;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            final String active = viewManagerModel.getActiveView();
            final boolean shouldBuildTrading =
                    active.equals(TradingViewModel.VIEW_NAME) && setupInput.isPresent();

            if (!shouldBuildTrading) {
                return;
            }

            final SetupInputData input = setupInput.get();
            final String ticker = input.getTicker();

            // --- 1. Session / Data Setup ---
            final InMemorySessionDataAccessObject sessionDAO =
                    new InMemorySessionDataAccessObject();

            final SupabaseTradeDataAccessObject tradeDAO =
                    new SupabaseTradeDataAccessObject();

            try {
                util.SupabaseRandomUserUtil.createAndLoginRandomUser(sessionDAO);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to create random user", e);
            }

            final String userId =
                    sessionDAO.getCurrentUserId().toString();

            // --- 2. Core Entity ---
            final Account account =
                    new Account(input.getInitialBalance(), userId);

            account.addTradeClosedListener(newRecord -> {
                try {
                    final UUID uuid = UUID.fromString(newRecord.getUserId());
                    tradeDAO.saveTrade(newRecord, uuid);
                }
                catch (RuntimeException ex) {
                    System.err.println("Failed to save trade: " + ex.getMessage());
                }
            });

            // --- 3. Use-Case Wiring ---

            final TradingPresenter tradingPresenter =
                    new TradingPresenter(tradingViewModel, viewManagerModel, setupViewModel);

            final UpdateMarketInteractor updateMarketInteractor =
                    new UpdateMarketInteractor(SIMULATION_DAO, tradingPresenter, account, ticker);

            updateMarketInteractor.setSpeed(input.getSpeedMultiplier());

            final SimulatedTradeInteractor tradeInteractor =
                    new SimulatedTradeInteractor(tradingPresenter, account);

            final TradingController tradingController =
                    new TradingController(updateMarketInteractor, tradeInteractor, tradingPresenter);

            // --- 4. View Construction ---

            views.removeAll();
            final TradingView tradingView =
                    new TradingView(tradingController, tradingViewModel);

            views.add(tradingView, TradingViewModel.VIEW_NAME);

            cardLayout.show(views, TradingViewModel.VIEW_NAME);

            views.revalidate();
            views.repaint();

            // --- 5. Start Engine ---
            final Thread loader = new Thread(() -> updateMarketInteractor.loadData(ticker));
            loader.start();

            setupInput = Optional.empty();
        }
    }

    /**
     * Final Presenter to capture Setup success and store input.
     */
    public static class FinalSetupPresenter extends SetupPresenter {

        public FinalSetupPresenter(ViewManagerModel viewManagerModel,
                                   TradingViewModel tradingViewModel,
                                   SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        @Override
        public void prepareSuccessView(SetupInputData input) {
            SimulatedMain.setupInput = Optional.of(input);
            super.prepareSuccessView(input);
        }
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {

        // Frame
        final JFrame application =
                new JFrame(TradingViewModel.TITLE_LABEL);

        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final CardLayout cardLayout = new CardLayout();

        final JPanel views = new JPanel(cardLayout);
        application.add(views);

        // View Models
        final ViewManagerModel viewManagerModel = new ViewManagerModel();
        final TradingViewModel tradingViewModel = new TradingViewModel();
        final SetupViewModel setupViewModel = new SetupViewModel();

        // Use-Case Assembly
        final SetupPresenter setupPresenter =
                new FinalSetupPresenter(viewManagerModel, tradingViewModel, setupViewModel);

        final SetupInteractor setupInteractor =
                new SetupInteractor(setupPresenter, SIMULATION_DAO);

        final SetupController setupController =
                new SetupController(setupInteractor);

        // Views
        final SetupView setupView = new SetupView(setupController, setupViewModel);
        final TradingView placeholderTradingView =
                new TradingView(null, tradingViewModel);

        views.add(setupView, SetupViewModel.VIEW_NAME);
        views.add(placeholderTradingView, TradingViewModel.VIEW_NAME);

        // Listener
        viewManagerModel.addPropertyChangeListener(
                new TradingViewFactoryListener(
                        views, cardLayout, tradingViewModel, viewManagerModel, setupViewModel
                )
        );

        new ViewManager(views, cardLayout, viewManagerModel);

        // Launch
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        application.setVisible(true);
    }
}
