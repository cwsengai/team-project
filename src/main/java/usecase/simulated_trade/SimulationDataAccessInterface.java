package usecase.simulated_trade;

import java.util.List;

import entity.PricePoint;

/**
 * Provides access to historical price data and generated tick data
 * used for simulation in the trading engine.
 */
public interface SimulationDataAccessInterface {

    /**
     * Loads historical price data for the specified ticker.
     *
     * @param ticker the stock symbol to load data for
     * @return a list of PricePoint objects representing historical data
     */
    List<PricePoint> loadHistory(String ticker);

    /**
     * Generates a list of simulated tick prices based on a given price point.
     *
     * @param point the base price point used for tick generation
     * @param numberOfTicks the number of tick values to generate
     * @return a list of generated price values
     */
    List<Double> generateTicks(PricePoint point, int numberOfTicks);
}
