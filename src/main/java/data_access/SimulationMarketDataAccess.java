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


    public SimulationMarketDataAccess(PriceDataAccessInterface realDataGateway) {
        this.realDataGateway = realDataGateway;
    }

    @Override
    public List<PricePoint> loadHistory(String ticker) {
        try {
            return realDataGateway.getPriceHistory(ticker, TimeInterval.FIVE_MINUTES);
        } catch (Exception e) {
            System.err.println("Simulation Data Load Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Double> generateTicks(PricePoint point, int numberOfTicks) {
        Double[] ticks = new Double[numberOfTicks];

        double open = point.getOpen();
        double high = point.getHigh();
        double low = point.getLow();
        double close = point.getClose();

        // 1. Anchor
        ticks[0] = open;
        ticks[numberOfTicks - 1] = close;

        // 2. Randomize positions (ensure valid bounds)
        if (numberOfTicks < 4) {
            fillInterpolation(ticks, 0, numberOfTicks - 1);
        } else {
            int idx1 = 1 + random.nextInt(numberOfTicks - 3);
            int idx2 = idx1 + 1 + random.nextInt(numberOfTicks - idx1 - 1);

            boolean highFirst = random.nextBoolean();
            ticks[idx1] = highFirst ? high : low;
            ticks[idx2] = highFirst ? low : high;

            fillInterpolation(ticks, 0, idx1);
            fillInterpolation(ticks, idx1, idx2);
            fillInterpolation(ticks, idx2, numberOfTicks - 1);
        }

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