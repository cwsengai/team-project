package use_case.simulated_trade;

import entity.PricePoint;
import java.util.List;

public interface SimulationDataAccessInterface {
    // load 5min data
    List<PricePoint> loadHistory(String ticker);

    // core! use 5min data to generate 60 data points
    List<Double> generateTicks(PricePoint point, int numberOfTicks);
}