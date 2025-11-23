package use_case.setup_simulation;

import java.time.LocalDate;

public class SetupInputData {
    private final String ticker;
    private final double initialBalance;
    private final int speedMultiplier;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public SetupInputData(String ticker, double initialBalance, int speedMultiplier,
                          LocalDate startDate, LocalDate endDate) {
        this.ticker = ticker;
        this.initialBalance = initialBalance;
        this.speedMultiplier = speedMultiplier;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters for the Interactor to use
    public String getTicker() { return ticker; }
    public double getInitialBalance() { return initialBalance; }
    public int getSpeedMultiplier() { return speedMultiplier; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}