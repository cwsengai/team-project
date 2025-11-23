package use_case.simulated_trade;


import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data_access.InMemorySessionDataAccessObject;
import data_access.SupabaseTradeDataAccessObject;
import entity.Account;
import interface_adapter.simulated_trading.TradingPresenter;
import interface_adapter.simulated_trading.TradingViewModel;
import use_case.session.SessionDataAccessInterface;


public class SimulatedTradeIntegrationTest {
    private SessionDataAccessInterface sessionDAO;
    private SupabaseTradeDataAccessObject tradeDAO;
    private SimulatedTradeInteractor tradeInteractor;
    private Account account;
    private TradingPresenter presenter;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setup() throws IOException {
        // Generate a unique email for each test run
        long rand = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        testEmail = "testuser" + rand + "@example.com";
        testPassword = "TestPassword123!";
        String jwt = SupabaseTestUtils.createUserAndGetJwt(testEmail, testPassword);
        sessionDAO = new InMemorySessionDataAccessObject();
        sessionDAO.setJwtToken(jwt);
        tradeDAO = new SupabaseTradeDataAccessObject();
        account = new Account(10000.0); // Use a random or fixed balance
        presenter = new TradingPresenter(new TradingViewModel());
        tradeInteractor = new SimulatedTradeInteractor(presenter, account, tradeDAO, sessionDAO);
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

    // TODO: Add a test for failure if no user is logged in
    @Test
    void testTradeSaveFailsIfNoUser() {
        sessionDAO.setJwtToken(null);
        String ticker = "AAPL";
        boolean isBuy = true;
        double amount = 1500.0;
        double price = 150.0;
        SimulatedTradeInputData input = new SimulatedTradeInputData(ticker, isBuy, amount, price);
        assertThrows(IllegalStateException.class, () ->
            tradeInteractor.executeTrade(input)
        );
    }
}
