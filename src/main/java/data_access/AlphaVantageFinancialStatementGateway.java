package data_access;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import api.Api;

import entity.FinancialStatement;
import use_case.financial_statement.FinancialStatementGateway;


public class AlphaVantageFinancialStatementGateway implements FinancialStatementGateway {
    private final Api api;

    public AlphaVantageFinancialStatementGateway(Api api){
        this.api = api;
    }

    @Override
    public List<FinancialStatement> fetchFinancialStatements(String symbol) {
        String jsonString_balance;
        try {
            jsonString_balance = api.getFuncBalanceSheet(symbol);
        } catch (Exception e) {
            e.printStackTrace(); return null;
        }

        String jsonString_income;
        try {
            jsonString_income = api.getFuncIncomeStatement(symbol);
        } catch (Exception e) {
            e.printStackTrace(); return null;
        }

        String jsonString_cashflow;
        try {
            jsonString_cashflow = api.getFuncCashFlow(symbol);
        } catch (Exception e) {
            e.printStackTrace(); return null;
        }

        Map<LocalDate, JSONObject> balanceMap = extractAnnualReports(jsonString_balance);
        Map<LocalDate, JSONObject> incomeMap  = extractAnnualReports(jsonString_income);
        Map<LocalDate, JSONObject> cashMap    = extractAnnualReports(jsonString_cashflow);

        // Get intersection of all available fiscal years
        List<LocalDate> commonDates = balanceMap.keySet().stream()
                .filter(incomeMap::containsKey)
                .filter(cashMap::containsKey)
                .sorted((d1, d2) -> d2.compareTo(d1)) // newest first
                .limit(5)
                .collect(Collectors.toList());

        List<FinancialStatement> list = new ArrayList<>();

        for (LocalDate date : commonDates) {
            JSONObject bal = balanceMap.get(date);
            JSONObject inc = incomeMap.get(date);
            JSONObject cas = cashMap.get(date);

            FinancialStatement fs = new FinancialStatement(
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

        return list;
    }

    private Map<LocalDate, JSONObject> extractAnnualReports(String jsonStr) {
        Map<LocalDate, JSONObject> map = new HashMap<>();

        JSONObject root = new JSONObject(jsonStr);
        if (!root.has("annualReports")) return map;

        JSONArray arr = root.getJSONArray("annualReports");

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            LocalDate date = LocalDate.parse(obj.getString("fiscalDateEnding"));
            map.put(date, obj);
        }

        return map;
    }




}
