package usecase.simulated_trade;

import entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UseCaseLayerTest {

    // Mock OutputBoundary (Presenter)
    private class MockPresenter implements SimulatedTradeOutputBoundary {
        SimulatedTradeOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(SimulatedTradeOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }
    }

    private MockPresenter presenter;
    private SimulatedTradeInteractor interactor;

    @BeforeEach
    void setUp() {
        Account account = new Account(2000.0, "user1");
        presenter = new MockPresenter();
        interactor = new SimulatedTradeInteractor(presenter, account);
    }

    // --- 1. Test Successful Trade (Also covers Input/Output Data Getters) ---
    @Test
    void testExecuteTradeSuccess() {
        // Prepare InputData
        SimulatedTradeInputData input = new SimulatedTradeInputData("AAPL", true, 1000.0, 100.0);

        // Test InputData Getters to ensure coverage
        assertEquals("AAPL", input.getTicker());
        assertTrue(input.isBuyAction());
        assertEquals(1000.0, input.getAmount());
        assertEquals(100.0, input.getCurrentPrice());

        // Execute
        interactor.executeTrade(input);

        // Verify Results
        assertNotNull(presenter.successData);
        assertEquals(1000.0, presenter.successData.getNewBalance()); // Balance decreases
        assertTrue(presenter.successData.getMessage().contains("Bought"));

        // Test OutputData Getters
        assertNotNull(presenter.successData.getMessage());
    }

    // --- 2. Test All Failure Branches ---

    @Test
    void testFailNegativeAmount() {
        // Branch 1: amount <= 0
        SimulatedTradeInputData input = new SimulatedTradeInputData("A", true, -50.0, 10.0);
        interactor.executeTrade(input);
        assertEquals("Amount must be positive.", presenter.failMessage);
    }

    @Test
    void testFailTooSmallAmount() {
        // Branch 2: quantity <= 0 (Not enough money to buy 1 share)
        SimulatedTradeInputData input = new SimulatedTradeInputData("A", true, 5.0, 100.0);
        interactor.executeTrade(input);
        assertEquals("Amount too low to buy 1 share.", presenter.failMessage);
    }

    @Test
    void testFailInsufficientFunds() {
        // Branch 3: Insufficient funds
        SimulatedTradeInputData input = new SimulatedTradeInputData("A", true, 5000.0, 10.0);
        interactor.executeTrade(input);
        assertEquals("Insufficient funds.", presenter.failMessage);
    }
}