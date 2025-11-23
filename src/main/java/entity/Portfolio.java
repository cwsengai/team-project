//package entity;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Represents a user's portfolio containing multiple positions and cash.
// * This entity is independent of infrastructure concerns and does not depend on data access interfaces.
// */
//public class Portfolio {
//    private final String id;
//    private final String userId;  // Changed from ownerId to match database
//    private String name;
//    private boolean isSimulation;
//    private final double initialCash;
//    private double currentCash;
//    private String currency;
//    private final LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private final List<Position> positions;
//
//    public Portfolio(String id, String userId, String name, boolean isSimulation,
//                     double initialCash, double currentCash, String currency,
//                     LocalDateTime createdAt, LocalDateTime updatedAt) {
//        this.id = id;
//        this.userId = userId;
//        this.name = name;
//        this.isSimulation = isSimulation;
//        this.initialCash = initialCash;
//        this.currentCash = currentCash;
//        this.currency = currency != null ? currency : "USD";
//        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
//        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
//        this.positions = new ArrayList<>();
//    }
//
//    // Backward compatible constructor for existing code
//    public Portfolio(String id, String userId, double initialCash) {
//        this(id, userId, "My Portfolio", true, initialCash, initialCash, "USD",
//             LocalDateTime.now(), LocalDateTime.now());
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    // Keep old method for backward compatibility
//    @Deprecated
//    public String getOwnerId() {
//        return userId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public boolean isSimulation() {
//        return isSimulation;
//    }
//
//    public void setSimulation(boolean simulation) {
//        isSimulation = simulation;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public double getInitialCash() {
//        return initialCash;
//    }
//
//    public double getCurrentCash() {
//        return currentCash;
//    }
//
//    public void setCurrentCash(double currentCash) {
//        this.currentCash = currentCash;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    // Keep old method for backward compatibility
//    @Deprecated
//    public double getCash() {
//        return currentCash;
//    }
//
//    @Deprecated
//    public void setCash(double cash) {
//        setCurrentCash(cash);
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public List<Position> getPositions() {
//        return new ArrayList<>(positions);
//    }
//
//    /**
//     * Add a position to the portfolio.
//     */
//    public void addPosition(Position position) {
//        positions.add(position);
//    }
//
//    /**
//     * Find a position by ticker symbol.
//     */
//    public Position findPosition(String ticker) {
//        return positions.stream()
//                .filter(p -> p.getTicker().equals(ticker))
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * TODO: Implement realized gains calculation across all positions
//     */
//    public double calculateRealizedGains() {
//        double totalRealizedGains = 0.0;
//        for (Position position : positions) {
//            totalRealizedGains += position.getRealizedGains();
//        }
//        return totalRealizedGains;
//    }
//
//    /**
//     * Calculate unrealized gains using current market prices.
//     * @param currentPrices Map of ticker symbol to current market price
//     * @return Total unrealized gains across all positions
//     */
//    public double calculateUnrealizedGains(Map<String, Double> currentPrices) {
//        double totalUnrealizedGains = 0.0;
//
//        // Calculate unrealized gains for each position
//        for (Position position : positions) {
//            Double currentPrice = currentPrices.get(position.getTicker());
//            if (currentPrice != null) {
//                totalUnrealizedGains += position.unrealizedGain(currentPrice);
//            }
//            // If price is not available, position contributes 0 to unrealized gains
//        }
//
//        return totalUnrealizedGains;
//    }
//
//    /**
//     * Calculate total portfolio value (market value of positions + cash).
//     * @param currentPrices Map of ticker symbol to current market price
//     * @return Total portfolio value
//     */
//    public double getTotalValue(Map<String, Double> currentPrices) {
//        double totalPositionsValue = 0.0;
//
//        for (Position position : positions) {
//            Double currentPrice = currentPrices.get(position.getTicker());
//            if (currentPrice != null) {
//                totalPositionsValue += position.currentMarketValue(currentPrice);
//            }
//        }
//
//        return totalPositionsValue + currentCash;
//    }
//}
