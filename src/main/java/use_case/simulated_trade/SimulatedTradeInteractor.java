package use_case.simulated_trade;

import entity.Account;

/**
 * Handles the business logic for executing a trade based on user input amount.
 */
public class SimulatedTradeInteractor implements SimulatedTradeInputBoundary {

    private final SimulatedTradeOutputBoundary presenter;
    private final Account account;

    public SimulatedTradeInteractor(SimulatedTradeOutputBoundary presenter, Account account) {
        this.presenter = presenter;
        this.account = account;
    }

    @Override
    public void executeTrade(SimulatedTradeInputData input) {
        String ticker = input.getTicker();
        double price = input.getCurrentPrice();
        double amountUSD = input.getAmount();
        boolean isBuy = input.isBuyAction();

        // Validate input amount
        if (amountUSD <= 0) {
            presenter.prepareFailView("Amount must be positive.");
            return;
        }

        // Calculate share quantity (floor value)
        int quantity = (int) (amountUSD / price);

        if (quantity <= 0) {
            presenter.prepareFailView("Amount too low to buy 1 share.");
            return;
        }

        // Check for sufficient funds (only required for Buy actions)
        double totalCost = quantity * price;
        if (isBuy && account.getBalance() < totalCost) {
            presenter.prepareFailView("Insufficient funds.");
            return;
        }

        // Execute trade logic in Entity
        account.executeTrade(ticker, isBuy, quantity, price);

        // Prepare success response
        String action = isBuy ? "Bought" : "Sold (Short)";
        String message = String.format("Successfully %s %d shares of %s at $%.2f",
                action, quantity, ticker, price);

        // Notify Presenter
        SimulatedTradeOutputData outputData = new SimulatedTradeOutputData(account.getBalance(), message);
        presenter.prepareSuccessView(outputData);
    }
}