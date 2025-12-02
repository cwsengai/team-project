package entity;

public class CompanyDetailViewModel {

    private static final double TRILLION_THRESHOLD = 1e12;
    private static final double BILLION_THRESHOLD = 1e9;
    private static final double MILLION_THRESHOLD = 1e6;

    private final String ticker;

    public CompanyDetailViewModel(Company company, FinancialStatement financials) {
        this.ticker = company.getSymbol();
        formatLargeNumber(company.getMarketCapitalization());
        // (e.g., $2.8T)

        if (financials != null) {
            formatLargeNumber(financials.getTotalRevenue());
            formatLargeNumber(financials.getNetIncome());
        }

    }

    public String getTicker() {
        return ticker;
    }

    private void formatLargeNumber(double number) {
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
    }
}
