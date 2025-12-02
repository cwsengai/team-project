package entity;

import java.util.List;

public class CompanyDetailViewModel {

    private static final String NOT_AVAILABLE = "N/A";
    private static final double TRILLION_THRESHOLD = 1e12;
    private static final double BILLION_THRESHOLD = 1e9;
    private static final double MILLION_THRESHOLD = 1e6;

    private final String ticker;

    public CompanyDetailViewModel(Company company, FinancialStatement financials, List<NewsArticle> news) {
        this.ticker = company.getSymbol();
        String name = company.getName();
        String sector = company.getSector();
        String marketCapFormatted = formatLargeNumber(company.getMarketCapitalization());
        // (e.g., $2.8T)
        String peRatioFormatted = String.format("%.2f", company.getPeRatio());

        String latestBalanceSheetPeriod;
        String latestNetIncome;
        String latestRevenue;
        if (financials != null) {
            formatLargeNumber(financials.getTotalRevenue());
            formatLargeNumber(financials.getNetIncome());
        }

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
