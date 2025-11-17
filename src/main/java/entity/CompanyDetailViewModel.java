package entity;

import java.util.List;

public class CompanyDetailViewModel {

    private final String ticker;
    private final String name;
    private final String sector;
    private final String marketCapFormatted; // (e.g., $2.8T)
    private final String peRatioFormatted;

    private final String latestRevenue;
    private final String latestNetIncome;
    private final String latestBalanceSheetPeriod;

    private final List<NewsArticle> recentNews;

    public CompanyDetailViewModel(Company company, FinancialStatement financials, List<NewsArticle> news) {
        this.ticker = company.getSymbol();
        this.name = company.getName();
        this.sector = company.getSector();
        this.marketCapFormatted = formatLargeNumber(company.getMarketCapitalization());
        this.peRatioFormatted = String.format("%.2f", company.getPeRatio());

        if (financials != null) {
            this.latestRevenue = formatLargeNumber(financials.getTotalRevenue());
            this.latestNetIncome = formatLargeNumber(financials.getNetIncome());
            this.latestBalanceSheetPeriod = financials.getFiscalDateEnding().toString();
        } else {
            this.latestRevenue = "N/A";
            this.latestNetIncome = "N/A";
            this.latestBalanceSheetPeriod = "N/A";
        }

        this.recentNews = news;
    }

    public String getTicker() { return ticker; }
    public String getName() { return name; }
    public String getSector() { return sector; }
    public String getMarketCapFormatted() { return marketCapFormatted; }
    public String getPeRatioFormatted() { return peRatioFormatted; }
    public String getLatestRevenue() { return latestRevenue; }
    public String getLatestNetIncome() { return latestNetIncome; }
    public String getLatestBalanceSheetPeriod() { return latestBalanceSheetPeriod; }
    public List<NewsArticle> getRecentNews() { return recentNews; }

    private String formatLargeNumber(double number) {
        if (number >= 1e12) return String.format("$%.1fT", number / 1e12);
        if (number >= 1e9) return String.format("$%.1fB", number / 1e9);
        if (number >= 1e6) return String.format("$%.1fM", number / 1e6);
        return String.format("$%.0f", number);
    }
}