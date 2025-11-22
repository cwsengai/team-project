package use_case.update_market;

import entity.Account;
import entity.PricePoint;
import use_case.simulated_trade.SimulationDataAccessInterface;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class UpdateMarketInteractor implements UpdateMarketInputBoundary {

    private final SimulationDataAccessInterface dataAccess;
    private final UpdateMarketOutputBoundary presenter;
    private final Account account;

    private List<PricePoint> allCandles;
    private List<Double> currentMinuteTicks;
    private int candleIndex = 0;
    private int tickIndex = 0;
    private int currentSpeed = 5;

    private final List<Double> historyTicksForChart = new ArrayList<>();

    public UpdateMarketInteractor(SimulationDataAccessInterface dataAccess,
                                  UpdateMarketOutputBoundary presenter,
                                  Account account,
                                  String ticker) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.account = account;
    }

    private int calculateTicksPerCandle() {
        if (currentSpeed < 1) currentSpeed = 1;
        return 300 / currentSpeed;
    }

    public void setSpeed(int speed) {
        this.currentSpeed = speed;
    }

    public void loadData(String ticker) {
        this.allCandles = dataAccess.loadHistory(ticker);

        if (allCandles != null && !allCandles.isEmpty()) {
            int ticksNeeded = calculateTicksPerCandle();
            this.currentMinuteTicks = dataAccess.generateTicks(allCandles.get(0), ticksNeeded);

            SwingUtilities.invokeLater(this::executeExecuteTick);
        } else {
            presenter.prepareFailView("Failed to load historical data. Check API/Network.");
        }
    }

    @Override
    public void executeExecuteTick() {
        if (allCandles == null || allCandles.isEmpty() || candleIndex >= allCandles.size()) {
            presenter.prepareFailView("Simulation Data Ended");
            return;
        }

        if (currentMinuteTicks == null || tickIndex >= currentMinuteTicks.size()) {
            presenter.prepareFailView("Tick Data Error");
            return;
        }
        double currentPrice = currentMinuteTicks.get(tickIndex);
        historyTicksForChart.add(currentPrice);

        String currentTicker = "AAPL";
        double currentEquity = account.calculateTotalEquity(currentPrice, currentTicker);

        UpdateMarketOutputData outputData = new UpdateMarketOutputData(
                currentPrice,
                currentEquity,
                account.getTotalReturnRate(currentEquity),
                account.getMaxDrawdown(currentEquity),
                account.getBalance(),
                historyTicksForChart,
                account.getPositions(),
                null
        );

        presenter.prepareSuccessView(outputData);

        tickIndex++;

        if (tickIndex >= currentMinuteTicks.size()) {
            tickIndex = 0;
            candleIndex++;

            if (candleIndex < allCandles.size()) {
                int ticks = calculateTicksPerCandle();
                currentMinuteTicks = dataAccess.generateTicks(allCandles.get(candleIndex), ticks);
            }
        }
    }
}