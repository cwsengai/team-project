package entity;

import java.util.List;

public class ChartViewModel {

    private final String title;
    // "DOW | DAILY Price"
    private final List<String> labels;
    // X
    private final List<Double> prices;
    // Y
    private final TimeInterval interval;
    // Time interval for sampling strategy
    
    // For candlestick charts (OHLC data)
    private final List<Double> openPrices;
    private final List<Double> highPrices;
    private final List<Double> lowPrices;
    private final List<Double> closePrices;
    private final boolean isCandlestick;

    // Constructor for line chart (simple price data)
    public ChartViewModel(String title, List<String> labels, List<Double> prices, TimeInterval interval) {
        this.title = title;
        this.labels = labels;
        this.prices = prices;
        this.interval = interval;
        this.openPrices = null;
        this.highPrices = null;
        this.lowPrices = null;
        this.closePrices = null;
        this.isCandlestick = false;
    }

    // Constructor for candlestick chart (OHLC data)
    public ChartViewModel(String title, List<String> labels, 
                         List<Double> openPrices, List<Double> highPrices, 
                         List<Double> lowPrices, List<Double> closePrices, TimeInterval interval) {
        this.title = title;
        this.labels = labels;
        this.prices = null;
        // Not used for candlestick
        this.interval = interval;
        this.openPrices = openPrices;
        this.highPrices = highPrices;
        this.lowPrices = lowPrices;
        this.closePrices = closePrices;
        this.isCandlestick = true;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public List<Double> getClosePrices() {
        return closePrices;
    }

    public boolean isCandlestick() {
        return isCandlestick;
    }

    public TimeInterval getInterval() {
        return interval;
    }
}
