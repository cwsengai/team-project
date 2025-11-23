package interface_adapter.setup_simulation;

import use_case.setup_simulation.SetupInputBoundary;
import use_case.setup_simulation.SetupInputData;

import java.time.LocalDate;

public class SetupController {
    private final SetupInputBoundary setupInteractor;

    public SetupController(SetupInputBoundary setupInteractor) {
        this.setupInteractor = setupInteractor;
    }

    public void execute(String ticker, double initialBalance, int speedMultiplier, String startDateStr, String endDateStr) {
        // Simple conversion for the Interactor
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        SetupInputData inputData = new SetupInputData(
                ticker, initialBalance, speedMultiplier, startDate, endDate
        );

        setupInteractor.execute(inputData);
    }
}