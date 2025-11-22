package use_case.price_simulation;

import entity.PricePoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PriceSimulationEngine {
    private List<PricePoint> rawData;   // PriceGateway get 5min data
    private int compressionFactor = 5;  //speed 5x/10x/20/30x
    private int speed = 1;                  // 1 / 2 / 5
    private int pointsPerCandle;
    private List<Double> timeline;

    // data load by controller
    public void loadRawData(List<PricePoint> data) {
        this.rawData = data;
    }

    public void setCompressionFactor(int compressionFactor) {
        this.compressionFactor = compressionFactor;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    //core logic
    public List<Double> getTimeline() {
        timeline = new ArrayList<>();
        if (rawData == null || rawData.isEmpty()) {
            return timeline;
        }
        int pointsPerCandle = compressionFactor * 60;
        for (PricePoint p : rawData) {
            List<Double> candleData = generateForOneCandle(p, pointsPerCandle);
            timeline.addAll(candleData);
        }
        return timeline;
    }

    public List<Double> generateTimeline() {

        List<Double> timeline = new ArrayList<>();

        for (PricePoint p : rawData) {
            List<Double> segment = generateForOneCandle(p, compressionFactor);
            timeline.addAll(segment);
        }

        return timeline;
    }

    //per CandleLine:
    private List<Double> generateForOneCandle(PricePoint p, int points) {
        List<Double> result = new ArrayList<>(Collections.nCopies(points, 0.0));
        double open = p.getOpen();
        double high = p.getHigh();
        double low = p.getLow();
        double close = p.getClose();
        //set price for open and close:
        result.set(0, open);
        result.set(points - 1, close);
        // radom assign high and low price
        int lowIndex = randomIndex(1, points - 2);
        int highIndex = randomIndex(1, points - 2);
        // just in case they have same index
        while (highIndex == lowIndex) {
            highIndex = randomIndex(1, points - 2);
        }
        result.set(lowIndex, low);
        result.set(highIndex, high);
        for (int i = 1; i < points - 1; i++) {
            if (i == lowIndex || i == highIndex) continue;

            double rand = low + Math.random() * (high - low);
            result.set(i, rand);
        }
        return result;
    }
    private int randomIndex(int min, int max) {
        return min + (int)(Math.random() * (max - min + 1));
    }
}
