package usecase.simulated_trade;

import java.time.LocalDateTime;

import entity.Account;

/**
 * Interactor responsible for executing a simulated trade.
 */
public class SimulatedTradeInteractor implements SimulatedTradeInputBoundary {

    private final SimulatedTradeOutputBoundary presenter;
    private final Account account;

    public SimulatedTradeInteractor(SimulatedTradeOutputBoundary presenter,
                                    Account account) {
        this.presenter = presenter;
        this.account = account;
    }

    @Override
    public void executeTrade(SimulatedTradeInputData inputData) {

        final String ticker = inputData.getTicker();
        final double price = inputData.getCurrentPrice();
        final double amount = inputData.getAmount();
        final boolean isBuyAction = inputData.isBuyAction();
        final LocalDateTime tradeTime = LocalDateTime.now();

        String errorMessage = validateInput(amount, price, isBuyAction);

        boolean success = false;
        SimulatedTradeOutputData outputData = null;

        if (errorMessage == null) {

            final int quantity = (int) (amount / price);
            final double totalCost = quantity * price;

            final boolean insufficientFunds =
                    isBuyAction && account.getBalance() < totalCost;

            if (!insufficientFunds) {

                account.executeTrade(ticker, isBuyAction, quantity, price, tradeTime);

                String actionLabel = "Sold";
                if (isBuyAction) {
                    actionLabel = "Bought";
                }

                final String successMessage = String.format(
                        "Successfully %s %d shares of %s at $%.2f",
                        actionLabel, quantity, ticker, price
                );

                outputData = new SimulatedTradeOutputData(
                        account.getBalance(),
                        successMessage
                );

                success = true;
            }
            else {
                errorMessage = "Insufficient funds.";
            }
        }

        if (success) {
            presenter.prepareSuccessView(outputData);
        }
        else {
            presenter.prepareFailView(errorMessage);
        }
    }

    /**
     * Validates trade parameters.
     *
     * @param amount trade amount
     * @param price current market price
     * @param isBuyAction true if buying
     * @return error message string or null if valid
     */
    private String validateInput(double amount, double price, boolean isBuyAction) {
        String error = null;

        final boolean nonPositiveAmount = amount <= 0;
        if (nonPositiveAmount) {
            error = "Amount must be positive.";
        }
        else {
            final int quantity = (int) (amount / price);
            final boolean tooSmallQuantity = quantity <= 0;
            if (tooSmallQuantity) {
                error = "Amount is too low to purchase one unit.";
            }
        }

        return error;
    }
}
