package app;

import api.AlphaVantagePriceGateway;
import data_access.SimulationMarketDataAccess;
import entity.Account;
import interface_adapter.ViewManagerModel;
import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;
import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupPresenter;
import interface_adapter.setup_simulation.SetupViewModel;
import use_case.PriceDataAccessInterface;
import use_case.simulated_trade.SimulationDataAccessInterface;
import use_case.simulated_trade.SimulatedTradeInteractor;
import use_case.update_market.UpdateMarketInteractor;
import use_case.setup_simulation.SetupInteractor;
import use_case.setup_simulation.SetupInputData; // 👈 解决编译问题
import view.TradingView;
import view.SetupView;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

public class SimulatedMain {

    private static final PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
    private static final SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);

    // Global state to temporarily hold setup input data after validation
    private static Optional<SetupInputData> setupInput = Optional.empty();

    // --- 1. The Factory Listener (Handles dynamic view creation and async start) ---
    private static class TradingViewFactoryListener implements PropertyChangeListener {

        private final JPanel views;
        private final CardLayout cardLayout;
        private final TradingViewModel tradingViewModel;
        private final ViewManagerModel viewManagerModel;

        public TradingViewFactoryListener(JPanel views, CardLayout cardLayout, TradingViewModel tradingViewModel, ViewManagerModel viewManagerModel) {
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

                // 1. 创建 Account Entity
                Account account = new Account(input.getInitialBalance());
                TradingPresenter tradingPresenter = new TradingPresenter(tradingViewModel);

                // 2. 创建 Interactor (现在是异步加载)
                UpdateMarketInteractor updateMarketInteractor = new UpdateMarketInteractor(
                        simulationDAO, tradingPresenter, account, ticker
                );
                updateMarketInteractor.setSpeed(input.getSpeedMultiplier()); // 设置速度

                SimulatedTradeInteractor tradeInteractor = new SimulatedTradeInteractor(
                        tradingPresenter, account
                );

                // 3. 创建 Controller
                TradingController tradingController = new TradingController(updateMarketInteractor, tradeInteractor);

                // 4. 移除并替换 View
                views.removeAll();

                // 5. 创建并添加真实的 Trading View (它内部会启动 Timer)
                TradingView tradingView = new TradingView(tradingController, tradingViewModel);
                views.add(tradingView, TradingViewModel.VIEW_NAME);

                // 6. 显示并刷新
                cardLayout.show(views, TradingViewModel.VIEW_NAME);
                views.revalidate();
                views.repaint();

                // 7. 异步启动数据加载 (核心：不阻塞 UI 线程)
                new Thread(() -> {
                    System.out.println("Starting asynchronous data loading for " + ticker + "...");
                    updateMarketInteractor.loadData(ticker);
                }).start();

                // 8. 清空 Setup 数据，防止重复创建
                setupInput = Optional.empty();
            }
        }
    }

    // --- 2. The Final Setup Presenter (Saves validated input data) ---
    public static class FinalSetupPresenter extends SetupPresenter {
        public FinalSetupPresenter(ViewManagerModel viewManagerModel, TradingViewModel tradingViewModel, SetupViewModel setupViewModel) {
            super(viewManagerModel, tradingViewModel, setupViewModel);
        }

        // 覆盖基类方法，并接收 SetupInputData 参数
        @Override
        public void prepareSuccessView(SetupInputData input) {
            // 1. 关键：将数据存入全局状态，供 Factory 使用
            SimulatedMain.setupInput = Optional.of(input);

            // 2. 直接调用 ViewManagerModel 切换 View (避免调用 super 导致的抽象方法错误)
            viewManagerModel.setActiveView(tradingViewModel.getViewName());
            viewManagerModel.firePropertyChanged();
        }

        // 我们也需要确保 prepareFailView 逻辑存在，否则基类会报错
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
        application.setSize(1000, 650);
        application.setVisible(true);
    }
}