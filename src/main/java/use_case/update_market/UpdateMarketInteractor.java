package use_case.update_market;

import entity.Account;
import entity.PricePoint;
import use_case.simulated_trade.SimulationDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public class UpdateMarketInteractor implements UpdateMarketInputBoundary {

    private final SimulationDataAccessInterface dataAccess;
    private final UpdateMarketOutputBoundary presenter;
    private final Account account;

    // Simulation State
    private List<PricePoint> allCandles;
    private List<Double> currentMinuteTicks;
    private int candleIndex = 0;
    private int tickIndex = 0;

    // Speed Control: Default 5x (Real-time 1 min = 5 min historical)
    // Formula: Ticks = 300 / speed
    private int currentSpeed = 5;

    // Chart Data accumulator
    private final List<Double> historyTicksForChart = new ArrayList<>();

    public UpdateMarketInteractor(SimulationDataAccessInterface dataAccess,
                                  UpdateMarketOutputBoundary presenter,
                                  Account account,
                                  String ticker) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.account = account;

        // 1. Load all historical 5-min candles
        this.allCandles = dataAccess.loadHistory(ticker);

        // 2. Pre-generate ticks for the first candle based on default speed
        if (allCandles != null && !allCandles.isEmpty()) {
            int ticksNeeded = calculateTicksPerCandle();
            this.currentMinuteTicks = dataAccess.generateTicks(allCandles.get(0), ticksNeeded);
        }
    }

    /**
     * Helper to calculate how many ticks to generate per 5-min candle.
     * 5 mins = 300 seconds.
     * If speed is 5x, we need 300/5 = 60 ticks.
     * If speed is 10x, we need 300/10 = 30 ticks.
     */
    private int calculateTicksPerCandle() {
        if (currentSpeed < 1) currentSpeed = 1; // Prevent division by zero
        return 300 / currentSpeed;
    }

    // Optional: Allow Controller to change speed dynamically
    public void setSpeed(int speed) {
        this.currentSpeed = speed;
        // The new speed will take effect on the NEXT candle rollover
    }

    @Override
    public void executeExecuteTick() {
        // 1. Safety Checks
        if (allCandles == null || allCandles.isEmpty() || candleIndex >= allCandles.size()) {
            presenter.prepareFailView("Simulation Data Ended");
            return;
        }

        // 2. Get Current Tick
        if (currentMinuteTicks == null || tickIndex >= currentMinuteTicks.size()) {
            // Guard against index out of bounds if speed changed drastically
            tickIndex = 0;
            candleIndex++;
            if (candleIndex < allCandles.size()) {
                int ticks = calculateTicksPerCandle();
                currentMinuteTicks = dataAccess.generateTicks(allCandles.get(candleIndex), ticks);
            } else {
                presenter.prepareFailView("Simulation Ended");
                return;
            }
        }

        double currentPrice = currentMinuteTicks.get(tickIndex);
        historyTicksForChart.add(currentPrice);

        // 3. Update Account Equity
        // Assuming single stock "AAPL"
        String currentTicker = "AAPL";
        double currentEquity = account.calculateTotalEquity(currentPrice, currentTicker);

        // 4. Prepare Output Data
        UpdateMarketOutputData outputData = new UpdateMarketOutputData(
                currentPrice,
                currentEquity,
                account.getTotalReturnRate(currentEquity),
                account.getMaxDrawdown(currentEquity),
                account.getBalance(),
                historyTicksForChart,
                null
        );

        // 5. Send to Presenter
        presenter.prepareSuccessView(outputData);

        // 6. Advance Time
        tickIndex++;

        // 7. Handle Candle Rollover
        // If we have consumed all ticks for this candle, move to next
        if (tickIndex >= currentMinuteTicks.size()) {
            tickIndex = 0;
            candleIndex++;

            if (candleIndex < allCandles.size()) {
                // Generate next batch of ticks using current speed
                int ticks = calculateTicksPerCandle();
                currentMinuteTicks = dataAccess.generateTicks(allCandles.get(candleIndex), ticks);
            }
        }
    }
}