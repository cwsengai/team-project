package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a user's portfolio containing multiple positions and cash.
 * This entity is independent of infrastructure concerns and does not depend on data access interfaces.
 */
public class Portfolio {
    private final String id;
    private final String ownerId;
    private final List<Position> positions;
    private double cash;

    public Portfolio(String id, String ownerId, double initialCash) {
        this.id = id;
        this.ownerId = ownerId;
        this.positions = new ArrayList<>();
        this.cash = initialCash;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    /**
     * Add a position to the portfolio.
     */
    public void addPosition(Position position) {
        positions.add(position);
    }

    /**
     * Find a position by ticker symbol.
     */
    public Position findPosition(String ticker) {
        return positions.stream()
                .filter(p -> p.getTicker().equals(ticker))
                .findFirst()
                .orElse(null);
    }

    /**
     * TODO: Implement realized gains calculation across all positions
     */
    public double calculateRealizedGains() {
        double totalRealizedGains = 0.0;
        for (Position position : positions) {
            totalRealizedGains += position.getRealizedGains();
        }
        return totalRealizedGains;
    }

    /**
     * Calculate unrealized gains using current market prices.
     * @param currentPrices Map of ticker symbol to current market price
     * @return Total unrealized gains across all positions
     */
    public double calculateUnrealizedGains(Map<String, Double> currentPrices) {
        double totalUnrealizedGains = 0.0;
        
        // Calculate unrealized gains for each position
        for (Position position : positions) {
            Double currentPrice = currentPrices.get(position.getTicker());
            if (currentPrice != null) {
                totalUnrealizedGains += position.unrealizedGain(currentPrice);
            }
            // If price is not available, position contributes 0 to unrealized gains
        }
        
        return totalUnrealizedGains;
    }

    /**
     * Calculate total portfolio value (market value of positions + cash).
     * @param currentPrices Map of ticker symbol to current market price
     * @return Total portfolio value
     */
    public double getTotalValue(Map<String, Double> currentPrices) {
        double totalPositionsValue = 0.0;
        
        for (Position position : positions) {
            Double currentPrice = currentPrices.get(position.getTicker());
            if (currentPrice != null) {
                totalPositionsValue += position.currentMarketValue(currentPrice);
            }
        }
        
        return totalPositionsValue + cash;
    }
}
