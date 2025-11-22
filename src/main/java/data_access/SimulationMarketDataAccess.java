package data_access;

import entity.PricePoint;
import entity.TimeInterval;
import use_case.PriceDataAccessInterface;
import use_case.simulated_trade.SimulationDataAccessInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimulationMarketDataAccess implements SimulationDataAccessInterface {

    private final PriceDataAccessInterface realDataGateway;
    private final Random random = new Random();

    // 60 ticks to simulate 1 second per tick
    private static final int TICKS_PER_CANDLE = 60;

    public SimulationMarketDataAccess(PriceDataAccessInterface realDataGateway) {
        this.realDataGateway = realDataGateway;
    }

    @Override
    public List<PricePoint> loadHistory(String ticker) {
        try {
            // Force 5-minute interval for simulation base data
            return realDataGateway.getPriceHistory(ticker, TimeInterval.FIVE_MINUTS);
        } catch (Exception e) {
            System.err.println("Simulation Data Load Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Double> generateTicks(PricePoint point) {
        Double[] ticks = new Double[TICKS_PER_CANDLE];

        double open = point.getOpen();
        double high = point.getHigh();
        double low = point.getLow();
        double close = point.getClose();

        // Anchor Start and End
        ticks[0] = open;
        ticks[TICKS_PER_CANDLE - 1] = close;

        // Randomize positions for High and Low (between index 1 and 58)
        int idx1 = 1 + random.nextInt(TICKS_PER_CANDLE - 3);
        int idx2 = idx1 + 1 + random.nextInt(TICKS_PER_CANDLE - idx1 - 1);

        // Randomize order (High first or Low first)
        boolean highFirst = random.nextBoolean();
        ticks[idx1] = highFirst ? high : low;
        ticks[idx2] = highFirst ? low : high;

        // Linear Interpolation to fill gaps
        fillInterpolation(ticks, 0, idx1);
        fillInterpolation(ticks, idx1, idx2);
        fillInterpolation(ticks, idx2, TICKS_PER_CANDLE - 1);

        List<Double> result = new ArrayList<>();
        Collections.addAll(result, ticks);
        return result;
    }

    private void fillInterpolation(Double[] array, int startIndex, int endIndex) {
        double startVal = array[startIndex];
        double endVal = array[endIndex];
        int steps = endIndex - startIndex;

        if (steps <= 0) return;
        double stepValue = (endVal - startVal) / steps;

        for (int i = 1; i < steps; i++) {
            array[startIndex + i] = startVal + (stepValue * i);
        }
    }
}