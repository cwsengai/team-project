package use_case.simulated_trade;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data_access.InMemorySessionDataAccessObject;
import data_access.SupabaseTradeDataAccessObject;
import use_case.session.SessionDataAccessInterface;


public class SimulatedTradeIntegrationTest {
    private SessionDataAccessInterface sessionDAO;
    private SupabaseTradeDataAccessObject tradeDAO;
    private SimulatedTradeInteractor tradeInteractor;
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
        tradeInteractor = new SimulatedTradeInteractor(tradeDAO, sessionDAO);
    }

    @Test
    void testTradeSaveWithSessionUser() {
        String ticker = "AAPL";
        boolean isLong = true;
        int quantity = 10;
        double entry = 150.0;
        double exit = 155.0;
        LocalDateTime entryTime = LocalDateTime.now().minusHours(1);
        LocalDateTime exitTime = LocalDateTime.now();
        assertDoesNotThrow(() -> tradeInteractor.execute(ticker, isLong, quantity, entry, exit, entryTime, exitTime));
    }

    // TODO: Add a test for failure if no user is logged in
    @Test
    void testTradeSaveFailsIfNoUser() {
        sessionDAO.setJwtToken(null);
        String ticker = "AAPL";
        boolean isLong = true;
        int quantity = 10;
        double entry = 150.0;
        double exit = 155.0;
        LocalDateTime entryTime = LocalDateTime.now().minusHours(1);
        LocalDateTime exitTime = LocalDateTime.now();
        assertThrows(IllegalStateException.class, () ->
            tradeInteractor.execute(ticker, isLong, quantity, entry, exit, entryTime, exitTime)
        );
    }
}
