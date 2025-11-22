package app;

import api.AlphaVantagePriceGateway;
import data_access.SimulationMarketDataAccess;
import entity.Account;
import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;
import use_case.PriceDataAccessInterface;
import use_case.simulated_trade.SimulationDataAccessInterface;
import use_case.simulated_trade.SimulatedTradeInteractor;
import use_case.update_market.UpdateMarketInteractor;
import view.TradingView;

import javax.swing.*;

public class SimulatedMain {
    public static void main(String[] args) {
        // Create the main window
        JFrame application = new JFrame("Simulated Trading System (Test Mode)");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Initialize Data Access (The Fuel)
        // Use the existing API Gateway from the team
        PriceDataAccessInterface baseGateway = new AlphaVantagePriceGateway();
        // Wrap it with the simulation adapter (handles interpolation)
        SimulationDataAccessInterface simulationDAO = new SimulationMarketDataAccess(baseGateway);

        // Initialize Entity (The Brain)
        // Start with $100,000 initial capital
        Account account = new Account(100000.00);

        // Initialize Interface Adapters (The Bridge)
        TradingViewModel viewModel = new TradingViewModel();
        TradingPresenter presenter = new TradingPresenter(viewModel);

        // Initialize Use Cases (The Engine)
        // Start simulation with "AAPL" (can be changed to "TSLA", "IBM", etc.)
        UpdateMarketInteractor updateMarketInteractor = new UpdateMarketInteractor(
                simulationDAO, presenter, account, "AAPL"
        );

        SimulatedTradeInteractor tradeInteractor = new SimulatedTradeInteractor(
                presenter, account
        );

        // Initialize Controller
        TradingController controller = new TradingController(updateMarketInteractor, tradeInteractor);

        // Initialize View (The Dashboard)
        TradingView view = new TradingView(controller, viewModel);

        // Show the application
        application.add(view);
        application.pack();
        application.setSize(1000, 600); // Set a reasonable size
        application.setVisible(true);

        System.out.println("Simulated Trading System Started...");
    }
}