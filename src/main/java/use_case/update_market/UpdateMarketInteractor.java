package use_case.update_market;

import entity.Account;
import entity.PricePoint;
import entity.Position;
import use_case.simulated_trade.SimulationDataAccessInterface;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateMarketInteractor implements UpdateMarketInputBoundary {

    private final SimulationDataAccessInterface dataAccess;
    private final UpdateMarketOutputBoundary presenter;
    private final Account account;

    private final String simulationTicker;

    // Simulation State
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
        this.simulationTicker = ticker;
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
        // --- Safety Checks ---
        if (allCandles == null || allCandles.isEmpty() || candleIndex >= allCandles.size()) {
            presenter.prepareFailView("Simulation Data Ended");
            return;
        }
        if (currentMinuteTicks == null || tickIndex >= currentMinuteTicks.size()) {
            presenter.prepareFailView("Tick Data Error");
            return;
        }

        double currentPrice = currentMinuteTicks.get(tickIndex);
        String currentTicker = this.simulationTicker;

        // Update Account and Get Equity
        double currentEquity = account.calculateTotalEquity(currentPrice, currentTicker);

        // Get Data Needed for Presenter/View
        Map<String, Position> currentPositions = account.getPositions();
        historyTicksForChart.add(currentPrice);

        // 3. Package and Send Output Data (13 Arguments total now)
        UpdateMarketOutputData outputData = new UpdateMarketOutputData(
                currentPrice,
                currentEquity,
                account.getTotalReturnRate(currentEquity),
                account.getMaxDrawdown(currentEquity),
                account.getBalance(),

                // --- Pass Stats ---
                account.getTotalTrades(),
                account.getWinningTrades(),
                account.getMaxGain(),
                account.getLosingTrades(),
                account.getWinRate(),

                historyTicksForChart,
                currentPositions,
                null // Error string
        );

        presenter.prepareSuccessView(outputData);

        // Advance Time
        tickIndex++;

        // Handle Candle Rollover
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