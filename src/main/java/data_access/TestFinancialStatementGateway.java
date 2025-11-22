package data_access;

import data_access.AlphaVantageFinancialStatementGateway;
import entity.FinancialStatement;
import api.Api;

import java.util.List;

public class TestFinancialStatementGateway {

    public static void main(String[] args) {


        String apiKey = "demo";

        Api api = new Api(apiKey);
        AlphaVantageFinancialStatementGateway gateway =
                new AlphaVantageFinancialStatementGateway(api);

        String symbol = "IBM"; // try a stable ticker with long history

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
            System.out.println("Year: " + fs.getFiscalDateEnding());
            System.out.println("Currency: " + fs.getCurrency());
            System.out.println("Revenue: " + fs.getTotalRevenue());
            System.out.println("Gross Profit: " + fs.getGrossProfit());
            System.out.println("Cost of Revenue: " + fs.getCostOfRevenue());
            System.out.println("Operating Expense: " + fs.getOperatingExpenses());
            System.out.println("Ebit: " + fs.getEbit());
            System.out.println("Net Income: " + fs.getNetIncome());
            System.out.println("Assets: " + fs.getTotalAssets());
            System.out.println("Liabilities: " + fs.getTotalLiabilities());
            System.out.println("Equity: " + fs.getTotalShareholderEquity());
            System.out.println("Operating Cashflow: " + fs.getOperatingCashFlow());
            System.out.println("Capital Expenditures: " + fs.getCapitalExpenditures());
            System.out.println("Cash Flow From Investing: " + fs.getCashFlowFromInvesting());
            System.out.println("Cash Flow From Financing: " + fs.getCashFlowFromFinancing());
            System.out.println("Dividents Paid: " + fs.getDividendPayout());
            System.out.println("-----------------------------------------\n");
        }

        System.out.println("Test completed.");
    }
}
