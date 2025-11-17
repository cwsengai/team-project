package entity;

import java.time.LocalDateTime;

/**
 * Represents a publicly traded company.
 * Contains metadata about the company including ticker symbol, name, and sector information.
 */
public class Company {
    private final String id;
    private final String ticker;
    private String name;
    private String sector;
    private String industry;
    private String exchange;
    private Double marketCap;
    private String description;
    private final LocalDateTime createdAt;

    public Company(String id, String ticker, String name, String sector, String industry,
                   String exchange, Double marketCap, String description, LocalDateTime createdAt) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        this.industry = industry;
        this.exchange = exchange;
        this.marketCap = marketCap;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Company(String id, String ticker, String name) {
        this(id, ticker, name, null, null, null, null, null, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
