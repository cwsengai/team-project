package dataaccess;

import java.util.List;

import api.Api;
import entity.FinancialStatement;

public class TestFinancialStatementGateway {

    /**
     * Entry point for testing the financial statement retrieval module.
     * Initializes the API client, constructs the financial statement gateway,
     * and retrieves the financial statements for a predefined stock symbol.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {

        String apiKey = "demo";

        Api api = new Api(apiKey);
        AlphaVantageFinancialStatementGateway gateway =
                new AlphaVantageFinancialStatementGateway(api);

        // try a stable ticker with long history
        String symbol = "IBM";

        System.out.println("Fetching financial statements for: " + symbol);

        List<FinancialStatement> statements = gateway.fetchFinancialStatements(symbol);

        if (statements == null) {
            System.out.println("Gateway returned null — likely API error.");
            return;
        }

        if (statements.isEmpty()) {
            System.out.println("No financial statements returned.");
            return;
        }

        System.out.println("Retrieved " + statements.size() + " statements.\n");

        for (FinancialStatement fs : statements) {
            System.out.println("Year: " + fs.fiscalDateEnding());
            System.out.println("Currency: " + fs.currency());
            System.out.println("Revenue: " + fs.totalRevenue());
            System.out.println("Gross Profit: " + fs.grossProfit());
            System.out.println("Cost of Revenue: " + fs.costOfRevenue());
            System.out.println("Operating Expense: " + fs.operatingExpenses());
            System.out.println("Ebit: " + fs.ebit());
            System.out.println("Net Income: " + fs.netIncome());
            System.out.println("Assets: " + fs.totalAssets());
            System.out.println("Liabilities: " + fs.totalLiabilities());
            System.out.println("Equity: " + fs.totalShareholderEquity());
            System.out.println("Operating Cashflow: " + fs.operatingCashFlow());
            System.out.println("Capital Expenditures: " + fs.capitalExpenditures());
            System.out.println("Cash Flow From Investing: " + fs.cashFlowFromInvesting());
            System.out.println("Cash Flow From Financing: " + fs.cashFlowFromFinancing());
            System.out.println("Dividents Paid: " + fs.dividendPayout());
            System.out.println("-----------------------------------------\n");
        }

        System.out.println("Test completed.");
    }
}
