package entity;

import java.time.LocalDateTime;

/**
 * Represents a single trade (buy or sell) of a stock.
 */
public class Trade {
    private final String id;  // Changed from tradeId
    private final String portfolioId;
    private final String positionId;
    private final String instrumentSymbol;  // Matches DB: instrument_symbol (references financial_instruments.symbol)
    
    // Not stored in trades table - available via join with financial_instruments
    // Using transient to exclude from Gson serialization
    private final transient String instrumentType;
    
    private final TradeType tradeType;  // Matches DB: trade_type
    private final int quantity;
    private final double price;
    private final double fees;
    private final LocalDateTime executedAt;  // Matches DB: executed_at
    private final LocalDateTime createdAt;  // Matches DB: created_at

    public Trade(String id, String portfolioId, String positionId, String instrumentSymbol, String instrumentType,
                 TradeType tradeType, int quantity, double price, double fees,
                 LocalDateTime executedAt, LocalDateTime createdAt) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.positionId = positionId;
        this.instrumentSymbol = instrumentSymbol;
        this.instrumentType = instrumentType;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.fees = fees;
        this.executedAt = executedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Backward compatible constructor for existing code
    public Trade(String tradeId, String ticker, int quantity, double price,
                 LocalDateTime timestamp, boolean isBuy) {
        this(tradeId, null, null, ticker, null,
             isBuy ? TradeType.BUY : TradeType.SELL,
             quantity, price, 0.0, timestamp, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    // Keep old method for backward compatibility
    @Deprecated
    public String getTradeId() {
        return id;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getPositionId() {
        return positionId;
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

    public TradeType getTradeType() {
        return tradeType;
    }

    // Keep old method for backward compatibility
    @Deprecated
    public boolean isBuy() {
        return tradeType == TradeType.BUY;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getFees() {
        return fees;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    // Keep old method for backward compatibility
    @Deprecated
    public LocalDateTime getTimestamp() {
        return executedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Calculate the total trade value (quantity * price).
     * @return Total trade value before fees
     */
    public double getTradeValue() {
        return quantity * price;
    }

    /**
     * Calculate the total cost/proceeds including fees.
     * For buys: cost = (quantity * price) + fees
     * For sells: proceeds = (quantity * price) - fees
     * @return Total amount
     */
    public double getTotalAmount() {
        double baseValue = quantity * price;
        return tradeType == TradeType.BUY ? baseValue + fees : baseValue - fees;
    }
}
