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

    // Internal simulation state
    private List<PricePoint> allCandles;
    private List<Double> currentMinuteTicks;
    private int candleIndex = 0;
    private int tickIndex = 0;

    // Accumulates ticks for the chart
    private final List<Double> historyTicksForChart = new ArrayList<>();

    public UpdateMarketInteractor(SimulationDataAccessInterface dataAccess,
                                  UpdateMarketOutputBoundary presenter,
                                  Account account,
                                  String ticker) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.account = account;

        // 1. Load all historical data
        this.allCandles = dataAccess.loadHistory(ticker);

        // 2. Pre-generate ticks for the first candle
        if (allCandles != null && !allCandles.isEmpty()) {
            this.currentMinuteTicks = dataAccess.generateTicks(allCandles.get(0));
        }
    }

    @Override
    public void executeExecuteTick() {
        // 1. Check if simulation ended
        if (allCandles == null || allCandles.isEmpty() || candleIndex >= allCandles.size()) {
            presenter.prepareFailView("Simulation Data Ended");
            return;
        }

        // 2. Get current tick price
        if (currentMinuteTicks == null || tickIndex >= currentMinuteTicks.size()) {
            presenter.prepareFailView("Tick Data Error");
            return;
        }
        double currentPrice = currentMinuteTicks.get(tickIndex);
        historyTicksForChart.add(currentPrice);

        // 3. Update Account Equity (Calculate PnL)
        String currentTicker = "AAPL"; // Currently supports single ticker
        double currentEquity = account.calculateTotalEquity(currentPrice, currentTicker);

        // 4. Prepare output data
        UpdateMarketOutputData outputData = new UpdateMarketOutputData(
                currentPrice,
                currentEquity,
                account.getTotalReturnRate(currentEquity),
                account.getMaxDrawdown(currentEquity),
                account.getBalance(),
                historyTicksForChart,
                null
        );

        // 5. Notify Presenter
        presenter.prepareSuccessView(outputData);

        // 6. Advance time
        tickIndex++;

        // Handle candle rollover (every 60 ticks)
        if (tickIndex >= currentMinuteTicks.size()) {
            tickIndex = 0;
            candleIndex++;

            if (candleIndex < allCandles.size()) {
                currentMinuteTicks = dataAccess.generateTicks(allCandles.get(candleIndex));
            }
        }
    }
}