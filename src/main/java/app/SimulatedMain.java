package app;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import entity.SimulatedTradeRecord;
import use_case.simulated_trade.TradeClosedListener;

import interface_adapter.view_model.ViewManagerModel;
import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupPresenter;
import interface_adapter.setup_simulation.SetupViewModel;
import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;

import use_case.price_chart.PriceDataAccessInterface;
import use_case.setup_simulation.SetupInputData;
import use_case.setup_simulation.SetupInteractor;
import use_case.simulated_trade.SimulatedTradeInteractor;
import use_case.simulated_trade.SimulationDataAccessInterface;
import use_case.update_market.UpdateMarketInteractor;

import view.SetupView;
import view.TradingView;
import view.ViewManager;

public class SimulatedMain {

    private static final PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
    private static final SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);
    private static Optional<SetupInputData> setupInput = Optional.empty();

    /**
     * Factory Listener: Handles Setup -> Trading transition
     */
    private static class TradingViewFactoryListener implements PropertyChangeListener {

        private final JPanel views;
        private final CardLayout cardLayout;
        private final TradingViewModel tradingViewModel;
        private final ViewManagerModel viewManagerModel;

        private final SetupViewModel setupViewModel;

        public TradingViewFactoryListener(JPanel views, CardLayout cardLayout,
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
            if (viewManagerModel.getActiveView().equals(TradingViewModel.VIEW_NAME) && setupInput.isPresent()) {
                SetupInputData input = setupInput.get();
                String ticker = input.getTicker();

                // --- 1. Environment Setup ---
                InMemorySessionDataAccessObject sessionDAO = new InMemorySessionDataAccessObject();
                SupabaseTradeDataAccessObject tradeDAO = new SupabaseTradeDataAccessObject();
                try {
                    util.SupabaseRandomUserUtil.createAndLoginRandomUser(sessionDAO);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create session", e);
                }

                String userId = sessionDAO.getCurrentUserId().toString();

                // --- 2. Core Entity Setup ---
                Account account = new Account(input.getInitialBalance(), userId);

                account.addTradeClosedListener(new TradeClosedListener() {
                    @Override
                    public void onTradeClosed(SimulatedTradeRecord record) {
                        System.out.println(">> Observer: Saving trade to Supabase...");
                        try {
                            UUID userUuid = UUID.fromString(record.getUserId());
                            tradeDAO.saveTrade(record, userUuid);
                        } catch (Exception e) {
                            System.err.println("DB Save Failed: " + e.getMessage());
                        }
                    }
                });

                // --- 3. Clean Architecture Assembly ---

                TradingPresenter tradingPresenter = new TradingPresenter(
                        tradingViewModel,
                        viewManagerModel,
                        setupViewModel
                );

                UpdateMarketInteractor updateMarketInteractor = new UpdateMarketInteractor(
                        simulationDAO, tradingPresenter, account, ticker
                );
                updateMarketInteractor.setSpeed(input.getSpeedMultiplier());

                SimulatedTradeInteractor tradeInteractor = new SimulatedTradeInteractor(
                        tradingPresenter, account
                );

                TradingController tradingController = new TradingController(
                        updateMarketInteractor, tradeInteractor, tradingPresenter
                );

                // --- 4. View Creation ---
                views.removeAll();

                TradingView tradingView = new TradingView(tradingController, tradingViewModel);
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
        public FinalSetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        @Override
        public void prepareSuccessView(SetupInputData input) {
            SimulatedMain.setupInput = Optional.of(input);
            super.prepareSuccessView(input);
        }
    }

    // Main Entry Point
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

        // Setup Assembly
        SetupPresenter finalSetupPresenter = new FinalSetupPresenter(
                viewManagerModel, tradingViewModel, setupViewModel
        );
        SetupInteractor setupInteractor = new SetupInteractor(
                finalSetupPresenter, simulationDAO
        );
        SetupController setupController = new SetupController(setupInteractor);

        // Init Views
        SetupView setupView = new SetupView(setupController, setupViewModel);
        view.TradingView tradingPlaceholder = new view.TradingView(null, tradingViewModel);

        views.add(setupView, SetupViewModel.VIEW_NAME);
        views.add(tradingPlaceholder, TradingViewModel.VIEW_NAME);

        viewManagerModel.addPropertyChangeListener(new TradingViewFactoryListener(
                views, cardLayout, tradingViewModel, viewManagerModel, setupViewModel
        ));

        new ViewManager(views, cardLayout, viewManagerModel);

        // Launch
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setSize(1300, 750);
        application.setVisible(true);
    }
}