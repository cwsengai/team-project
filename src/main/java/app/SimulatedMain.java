package app;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import api.AlphaVantagePriceGateway;
import data_access.SimulationMarketDataAccess;
import interface_adapter.ViewManagerModel;
import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupPresenter;
import interface_adapter.setup_simulation.SetupViewModel;
import interface_adapter.simulated_trading.TradingViewModel;
import use_case.PriceDataAccessInterface;
import use_case.setup_simulation.SetupInputData;
import use_case.setup_simulation.SetupInteractor;
import use_case.simulated_trade.SimulationDataAccessInterface;
import view.SetupView;
import view.TradingView;
import view.ViewManager;


// import interface_adapter.simulated_trading.TradingController;
// import java.awt.CardLayout;





// import interface_adapter.setup_simulation.SetupController;
// import interface_adapter.setup_simulation.SetupPresenter;
// import interface_adapter.setup_simulation.SetupViewModel;
// import interface_adapter.simulated_trading.TradingViewModel;
// import use_case.PriceDataAccessInterface;
// import use_case.simulated_trade.SimulationDataAccessInterface;


public class SimulatedMain {

    private static final PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
    private static final SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);

    private static Optional<SetupInputData> setupInput = Optional.empty();

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
            if (viewManagerModel.getActiveView().equals(TradingViewModel.VIEW_NAME) && setupInput.isPresent()) {
                SetupInputData input = setupInput.get();
                String ticker = input.getTicker();

                // Create a random session and account for the user
                data_access.InMemorySessionDataAccessObject sessionDAO = new data_access.InMemorySessionDataAccessObject();
                try {
                    util.SupabaseRandomUserUtil.createAndLoginRandomUser(sessionDAO);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create random user for session", e);
                }
                entity.Account account = new entity.Account(input.getInitialBalance());
                data_access.SupabaseTradeDataAccessObject tradeDAO = new data_access.SupabaseTradeDataAccessObject();
                interface_adapter.simulated_trading.TradingPresenter tradingPresenter = new interface_adapter.simulated_trading.TradingPresenter(tradingViewModel);

                use_case.update_market.UpdateMarketInteractor updateMarketInteractor = new use_case.update_market.UpdateMarketInteractor(
                        simulationDAO, tradingPresenter, account, ticker
                );
                updateMarketInteractor.setSpeed(input.getSpeedMultiplier());

                use_case.simulated_trade.SimulatedTradeInteractor tradeInteractor = new use_case.simulated_trade.SimulatedTradeInteractor(
                        tradingPresenter, account, tradeDAO, sessionDAO
                );

                interface_adapter.simulated_trading.TradingController tradingController = new interface_adapter.simulated_trading.TradingController(updateMarketInteractor, tradeInteractor);

                views.removeAll();

                view.TradingView tradingView = new view.TradingView(tradingController, tradingViewModel);
                views.add(tradingView, TradingViewModel.VIEW_NAME);

                cardLayout.show(views, TradingViewModel.VIEW_NAME);
                views.revalidate();
                views.repaint();

                new Thread(() -> {
                    System.out.println("Starting asynchronous data loading for " + ticker + "...");
                    updateMarketInteractor.loadData(ticker);
                }).start();

                setupInput = Optional.empty();
            }
        }
    }

    // --- 2. The Final Setup Presenter (Saves validated input data) ---
    public static class FinalSetupPresenter extends SetupPresenter {
        public FinalSetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        @Override
        public void prepareSuccessView(SetupInputData input) {
            SimulatedMain.setupInput = Optional.of(input);

            viewManagerModel.setActiveView(tradingViewModel.getViewName());
            viewManagerModel.firePropertyChanged();
        }

        @Override
        public void prepareFailView(String error) {
            super.prepareFailView(error);
        }
    }

    public static void main(String[] args) {
        // 1. Set up main JFrame and CardLayout container
        JFrame application = new JFrame(TradingViewModel.TITLE_LABEL);
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel views = new JPanel(cardLayout);
        application.add(views);

        // 2. Initialize Models and ViewModels
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        TradingViewModel tradingViewModel = new TradingViewModel();
        SetupViewModel setupViewModel = new SetupViewModel();

        // 3. Assemble Setup Use Case
        SetupPresenter finalSetupPresenter = new FinalSetupPresenter(
                viewManagerModel, tradingViewModel, setupViewModel
        );
        SetupInteractor setupInteractor = new SetupInteractor(
                finalSetupPresenter, simulationDAO
        );
        SetupController setupController = new SetupController(setupInteractor);

        // 4. Create View
        SetupView setupView = new SetupView(setupController, setupViewModel);
        TradingView tradingPlaceholder = new TradingView(null, tradingViewModel);

        // 5. Add Views to CardLayout
        views.add(setupView, SetupViewModel.VIEW_NAME);
        views.add(tradingPlaceholder, TradingViewModel.VIEW_NAME);

        // 6. Bind View Manager and Factory Listener
        viewManagerModel.addPropertyChangeListener(new TradingViewFactoryListener(
                views, cardLayout, tradingViewModel, viewManagerModel
        ));

        new ViewManager(views, cardLayout, viewManagerModel);

        // 7. Initial Launch
        viewManagerModel.setActiveView(SetupViewModel.VIEW_NAME);
        viewManagerModel.firePropertyChanged();

        application.pack();
        application.setSize(1300, 750);
        application.setVisible(true);
    }
}