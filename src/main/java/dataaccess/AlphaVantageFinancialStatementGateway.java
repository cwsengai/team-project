package dataaccess;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Api;
import entity.FinancialStatement;
import usecase.financial_statement.FinancialStatementGateway;

public class AlphaVantageFinancialStatementGateway implements FinancialStatementGateway {
    private final Api api;

    public AlphaVantageFinancialStatementGateway(Api api) {
        this.api = api;
    }

    @Override
    public List<FinancialStatement> fetchFinancialStatements(String symbol) {

        String jsonstringbalance = null;
        try {
            jsonstringbalance = api.getFuncBalanceSheet(symbol);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        String jsonstringincome = null;
        try {
            jsonstringincome = api.getFuncIncomeStatement(symbol);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        String jsonstringcashflow = null;
        try {
            jsonstringcashflow = api.getFuncCashFlow(symbol);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        final List<FinancialStatement> list = new ArrayList<>();
        if (jsonstringbalance != null && jsonstringincome != null && jsonstringcashflow != null) {
            final Map<LocalDate, JSONObject> balanceMap = extractAnnualReports(jsonstringbalance);
            final Map<LocalDate, JSONObject> incomeMap = extractAnnualReports(jsonstringincome);
            final Map<LocalDate, JSONObject> cashMap = extractAnnualReports(jsonstringcashflow);

            final int limit = 5;

            // Get intersection of all available fiscal years
            final List<LocalDate> commonDates = balanceMap.keySet().stream()
                    .filter(incomeMap::containsKey)
                    .filter(cashMap::containsKey)
                    .sorted((firstdate, seconddate) -> seconddate.compareTo(firstdate))
                    .limit(limit)
                    .collect(Collectors.toList());

            for (LocalDate date : commonDates) {
                final JSONObject bal = balanceMap.get(date);
                final JSONObject inc = incomeMap.get(date);
                final JSONObject cas = cashMap.get(date);

                final FinancialStatement fs = new FinancialStatement(
                        symbol,
                        bal.optString("reportedCurrency", "USD"),
                        date,

                        // balance
                        bal.optLong("totalAssets", 0),
                        bal.optLong("totalLiabilities", 0),
                        bal.optLong("totalShareholderEquity", 0),

                        // income
                        inc.optLong("totalRevenue", 0),
                        inc.optLong("grossProfit", 0),
                        inc.optLong("costOfRevenue", 0),
                        inc.optLong("operatingExpenses", 0),
                        inc.optLong("ebit", 0),
                        inc.optLong("netIncome", 0),

                        // cash flow
                        cas.optLong("operatingCashflow", 0),
                        cas.optLong("capitalExpenditures", 0),
                        cas.optLong("cashflowFromInvestment", 0),
                        cas.optLong("cashflowFromFinancing", 0),
                        cas.optLong("dividendPayout", 0)
                );

                list.add(fs);
            }
        }
        return list;
    }

    private Map<LocalDate, JSONObject> extractAnnualReports(String jsonStr) {
        final Map<LocalDate, JSONObject> map = new HashMap<>();

        final JSONObject root = new JSONObject(jsonStr);
        if (root.has("annualReports")) {

            final JSONArray arr = root.getJSONArray("annualReports");

            for (int i = 0; i < arr.length(); i++) {
                final JSONObject obj = arr.getJSONObject(i);
                final LocalDate date = LocalDate.parse(obj.getString("fiscalDateEnding"));
                map.put(date, obj);
            }
        }

        return map;
    }

}
