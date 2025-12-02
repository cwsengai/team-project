package usecase.update_market;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import entity.Account;
import entity.Position;
import entity.PricePoint;
import usecase.simulated_trade.SimulationDataAccessInterface;

/**
 * Interactor for the UpdateMarket use case.
 *
 * <p>This class drives the simulated market tick updates, loads historical
 * price data, advances time, updates the account, and sends formatted
 * output to the presenter.</p>
 */
public class UpdateMarketInteractor implements UpdateMarketInputBoundary {

    private final SimulationDataAccessInterface dataAccess;
    private final UpdateMarketOutputBoundary presenter;
    private final Account account;

    private final String simulationTicker;

    private List<PricePoint> allCandles;
    private List<Double> currentMinuteTicks;
    private int candleIndex = 0;
    private int tickIndex = 0;
    private int currentSpeed = 5;

    private final List<Double> historyTicksForChart = new ArrayList<>();

    /**
     * Constructs a new UpdateMarketInteractor.
     *
     * @param dataAccess the data loader for candles and ticks
     * @param presenter the output boundary for displaying results
     * @param account the trading account being updated
     * @param ticker the simulated ticker symbol
     */
    public UpdateMarketInteractor(SimulationDataAccessInterface dataAccess,
                                  UpdateMarketOutputBoundary presenter,
                                  Account account,
                                  String ticker) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.account = account;
        this.simulationTicker = ticker;
    }

    // private helper; no Javadoc required for CSC207
    private int calculateTicksPerCandle() {
        if (currentSpeed < 1) {
            currentSpeed = 1;
        }
        return 300 / currentSpeed;
    }

    /**
     * Updates the simulation speed.
     *
     * @param speed the new speed value
     */
    public void setSpeed(int speed) {
        this.currentSpeed = speed;
    }

    /**
     * Loads historical candle data and initializes the first minute of ticks.
     *
     * @param ticker the asset symbol to load data for
     */
    public void loadData(String ticker) {
        this.allCandles = dataAccess.loadHistory(ticker);

        if (allCandles != null && !allCandles.isEmpty()) {
            int ticksNeeded = calculateTicksPerCandle();
            this.currentMinuteTicks = dataAccess.generateTicks(allCandles.getFirst(), ticksNeeded);

            SwingUtilities.invokeLater(this::executeExecuteTick);
        }
        else {
            presenter.prepareFailView("Failed to load historical data. Check API/Network.");
        }
    }

    /**
     * Executes one simulated market tick.
     *
     * <p>This method retrieves the current price, updates the account,
     * constructs output data, and advances internal simulation time.</p>
     */
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

        double currentEquity = account.calculateTotalEquity(currentPrice, this.simulationTicker);

        Map<String, Position> currentPositions = account.getPositions();
        historyTicksForChart.add(currentPrice);

        UpdateMarketOutputData outputData = new UpdateMarketOutputData(
                currentPrice,
                currentEquity,
                account.getTotalReturnRate(currentEquity),
                account.getMaxDrawdown(),
                account.getBalance(),
                account.getTotalTrades(),
                account.getWinningTrades(),
                account.getMaxGain(),
                account.getLosingTrades(),
                account.getWinRate(),
                historyTicksForChart,
                currentPositions
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
