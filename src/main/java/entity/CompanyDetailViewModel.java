package entity;

import java.util.List;

public class CompanyDetailViewModel {

    private static final String NOT_AVAILABLE = "N/A";
    private static final double TRILLION_THRESHOLD = 1e12;
    private static final double BILLION_THRESHOLD = 1e9;
    private static final double MILLION_THRESHOLD = 1e6;

    private final String ticker;
    private final String name;
    private final String sector;
    private final String marketCapFormatted;
    // (e.g., $2.8T)
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
        }
        else {
            this.latestRevenue = NOT_AVAILABLE;
            this.latestNetIncome = NOT_AVAILABLE;
            this.latestBalanceSheetPeriod = NOT_AVAILABLE;
        }

        this.recentNews = news;
    }

    public String getTicker() {
        return ticker;
    }

    private String formatLargeNumber(double number) {
        final String formattedNumber;
        if (number >= TRILLION_THRESHOLD) {
            formattedNumber = String.format("$%.1fT", number / TRILLION_THRESHOLD);
        }
        else if (number >= BILLION_THRESHOLD) {
            formattedNumber = String.format("$%.1fB", number / BILLION_THRESHOLD);
        }
        else if (number >= MILLION_THRESHOLD) {
            formattedNumber = String.format("$%.1fM", number / MILLION_THRESHOLD);
        }
        else {
            formattedNumber = String.format("$%.0f", number);
        }
        return formattedNumber;
    }
}
