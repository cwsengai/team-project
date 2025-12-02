package usecase.simulated_trade;


import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.InMemorySessionDataAccessObject;
import dataaccess.SupabaseTradeDataAccessObject;
import entity.Account;
import interfaceadapter.simulated_trading.TradingPresenter;
import interfaceadapter.simulated_trading.TradingViewModel;
import interfaceadapter.view_model.ViewManagerModel;
import usecase.session.SessionDataAccessInterface;


public class SimulatedTradeIntegrationTest {
    private SessionDataAccessInterface sessionDAO;
    private SupabaseTradeDataAccessObject tradeDAO;
    private SimulatedTradeInteractor tradeInteractor;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() throws IOException {
        // Generate a unique email for each test run
        long rand = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        String testEmail = "testuser" + rand + "@example.com";
        String testPassword = "TestPassword123!";
        String jwt = SupabaseTestUtils.createUserAndGetJwt(testEmail, testPassword);
        sessionDAO = new InMemorySessionDataAccessObject();
        sessionDAO.setJwtToken(jwt);
        tradeDAO = new SupabaseTradeDataAccessObject();
        String userId = sessionDAO.getCurrentUserId().toString();
        Account account = new Account(10000.0, userId); // Pass userId to Account
        // Register the trade save observer (mimic SimulatedMain)
        account.addTradeClosedListener(record -> {
            java.util.UUID userUuid = sessionDAO.getCurrentUserId();
            tradeDAO.saveTrade(record, userUuid);
        });
        TradingPresenter presenter = new TradingPresenter(
                new TradingViewModel(),
                new ViewManagerModel(),
                new interfaceadapter.setup_simulation.SetupViewModel()
        );
        tradeInteractor = new SimulatedTradeInteractor(presenter, account);
    }

    @Test
    void testTradeSaveWithSessionUser() {
        String ticker = "AAPL";
        boolean isBuy = true;
        double amount = 1500.0;
        double price = 150.0;
        SimulatedTradeInputData input = new SimulatedTradeInputData(ticker, isBuy, amount, price);
        assertDoesNotThrow(() -> tradeInteractor.executeTrade(input));
    }
    
    @Test
    void testTradeSaveFailsIfNoUser() {
        String ticker = "AAPL";
        double amount = 1500.0;
        double buyPrice = 150.0;
        double sellPrice = 151.0; // Use a different price to guarantee nonzero realizedPnL
        // Step 1: Buy to open a position (while logged in)
        SimulatedTradeInputData buyInput = new SimulatedTradeInputData(ticker, true, amount, buyPrice);
        tradeInteractor.executeTrade(buyInput);

        // Calculate the quantity actually bought
        int quantity = (int) (amount / buyPrice);

        // Step 2: Remove JWT to simulate logout
        sessionDAO.setJwtToken(null);

        // Step 3: Sell the exact quantity to close the position at a different price
        double sellAmount = quantity * sellPrice;
        SimulatedTradeInputData sellInput = new SimulatedTradeInputData(ticker, false, sellAmount, sellPrice);
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> tradeInteractor.executeTrade(sellInput));
    }
}
