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
    
    // Financial metrics
    private long marketCapitalization;
    private float eps;
    private float peRatio;
    private float dividendPerShare;
    private float dividendYield;
    private float beta;
    
    // Related entities
    private final List<FinancialStatement> financialStatements;
    private final List<NewsArticle> newsArticles;

    // Full constructor
    public Company(String symbol, String name, String description, String sector,
                   String industry, String country, long marketCapitalization,
                   float eps, float peRatio, float dividendPerShare,
                   float dividendYield, float beta,
                   List<FinancialStatement> financialStatements,
                   List<NewsArticle> newsArticles) {
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.industry = industry;
        this.country = country;
        this.marketCapitalization = marketCapitalization;
        this.eps = eps;
        this.peRatio = peRatio;
        this.dividendPerShare = dividendPerShare;
        this.dividendYield = dividendYield;
        this.beta = beta;
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
        this.marketCapitalization = (long) marketCap;
        this.eps = 0.0f;
        this.peRatio = (float) peRatio;
        this.dividendPerShare = 0.0f;
        this.dividendYield = 0.0f;
        this.beta = 1.0f;
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

    public float getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(long value) {
        this.marketCapitalization = value;
    }

    public float getEps() {
        return eps;
    }

    public void setEps(float value) {
        this.eps = value;
    }

    public float getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(float value) {
        this.peRatio = value;
    }

    public float getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(float value) {
        this.dividendPerShare = value;
    }

    public float getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(float value) {
        this.dividendYield = value;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float value) {
        this.beta = value;
    }

    public List<FinancialStatement> getFinancialStatements() {
        return financialStatements;
    }

    public List<NewsArticle> getNewsArticles() {
        return newsArticles;
    }
}
