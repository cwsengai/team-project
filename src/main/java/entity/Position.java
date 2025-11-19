package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a position in a particular stock within a portfolio.
 */
public class Position {
    private final String id;
    private final String portfolioId;
    private final String instrumentSymbol;  // Matches DB: instrument_symbol (references financial_instruments.symbol)
    private final String instrumentType;  // Matches DB: instrument_type
    private int quantity;
    private double averageCost;  // Matches DB: avg_price
    private double realizedPL;  // Matches DB: realized_pl
    private double unrealizedPL;  // Matches DB: unrealized_pl
    private LocalDateTime lastUpdated;  // Matches DB: last_updated
    private final List<Trade> trades;

    public Position(String id, String portfolioId, String instrumentSymbol, String instrumentType,
                    int quantity, double averageCost, double realizedPL, double unrealizedPL,
                    LocalDateTime lastUpdated) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.instrumentSymbol = instrumentSymbol;
        this.instrumentType = instrumentType;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.realizedPL = realizedPL;
        this.unrealizedPL = unrealizedPL;
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
        this.trades = new ArrayList<>();
    }

    // Backward compatible constructor for existing code
    public Position(String ticker) {
        this(null, null, ticker, null, 0, 0.0, 0.0, 0.0, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    // Backward compatibility - alias for instrumentSymbol
    @Deprecated
    public String getTicker() {
        return instrumentSymbol;
    }

    // Backward compatibility - companyId not used in new schema
    @Deprecated
    public String getCompanyId() {
        return null;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public double getRealizedPL() {
        return realizedPL;
    }

    public void setRealizedPL(double realizedPL) {
        this.realizedPL = realizedPL;
        this.lastUpdated = LocalDateTime.now();
    }

    public double getUnrealizedPL() {
        return unrealizedPL;
    }

    public void setUnrealizedPL(double unrealizedPL) {
        this.unrealizedPL = unrealizedPL;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }

    /**
     * Add a trade to this position and update quantity and average cost.
     */
    public void addTrade(Trade trade) {
        trades.add(trade);
        
        if (trade.getTradeType() == TradeType.BUY) {
            // Calculate new average cost when buying
            double totalCost = (averageCost * quantity) + (trade.getPrice() * trade.getQuantity());
            quantity += trade.getQuantity();
            averageCost = quantity > 0 ? totalCost / quantity : 0;
        } else {
            // Selling - calculate realized gains
            double saleProceeds = trade.getQuantity() * trade.getPrice();
            double costBasis = trade.getQuantity() * averageCost;
            realizedPL += (saleProceeds - costBasis - trade.getFees());
            quantity -= trade.getQuantity();
        }
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Calculate current market value of this position.
     */
    public double currentMarketValue(double currentPrice) {
        return quantity * currentPrice;
    }

    /**
     * Calculate unrealized gain/loss for this position.
     * TODO: Verify calculation logic
     */
    public double unrealizedGain(double currentPrice) {
        return (currentPrice - averageCost) * quantity;
    }

    /**
     * Get realized gains from closed trades.
     * @return Total realized gains/losses
     */
    public double getRealizedGains() {
        return realizedPL;
    }
}
