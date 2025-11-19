package entity;

import java.util.List;

/**
 * Represents a publicly traded company.
 * Clean architecture entity combining business logic needs.
 * Uses symbol as the primary identifier (no database IDs).
 */
public class Company {
    private final String symbol;  // Primary key
    private String name;
    private String description;
    private String sector;
    private String industry;
    private String country;
    private String exchange;
    
    // Financial metrics (using Double to match Supabase numeric type)
    private Double marketCapitalization;  // market_cap in DB
    private Double eps;
    private Double peRatio;  // pe_ratio in DB
    private Double dividendPerShare;  // dividend_per_share in DB
    private Double dividendYield;  // dividend_yield in DB
    private Double beta;
    
    // Related entities
    private transient final List<FinancialStatement> financialStatements;
    private transient final List<NewsArticle> newsArticles;

    // Full constructor
    public Company(String symbol, String name, String description, String sector,
                   String industry, String country, String exchange, Double marketCapitalization,
                   Double eps, Double peRatio, Double dividendPerShare,
                   Double dividendYield, Double beta,
                   List<FinancialStatement> financialStatements,
                   List<NewsArticle> newsArticles) {
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.industry = industry;
        this.country = country;
        this.exchange = exchange;
        this.marketCapitalization = marketCapitalization;
        this.eps = eps;
        this.peRatio = peRatio;
        this.dividendPerShare = dividendPerShare;
        this.dividendYield = dividendYield;
        this.beta = beta != null ? beta : 1.0;
        this.financialStatements = financialStatements;
        this.newsArticles = newsArticles;
    }

    // Simple constructor for basic company info
    public Company(String symbol, String name, String description, double marketCap, double peRatio) {
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.sector = null;
        this.industry = null;
        this.country = null;
        this.exchange = null;
        this.marketCapitalization = marketCap;
        this.eps = null;
        this.peRatio = peRatio;
        this.dividendPerShare = null;
        this.dividendYield = null;
        this.beta = 1.0;
        this.financialStatements = null;
        this.newsArticles = null;
    }

    // Minimal constructor
    public Company(String symbol, String name) {
        this(symbol, name, null, 0.0, 0.0);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Double getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(Double value) {
        this.marketCapitalization = value;
    }

    public Double getEPS() {
        return eps;
    }

    public void setEps(Double value) {
        this.eps = value;
    }

    public Double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(Double value) {
        this.peRatio = value;
    }

    public Double getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(Double value) {
        this.dividendPerShare = value;
    }

    public Double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Double value) {
        this.dividendYield = value;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double value) {
        this.beta = value;
    }

    public List<FinancialStatement> getFinancialStatements() {
        return financialStatements;
    }

    public List<NewsArticle> getNewsArticles() {
        return newsArticles;
    }
}
