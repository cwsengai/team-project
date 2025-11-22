package entity;

import java.time.LocalDate;


public class FinancialStatement {
    private final String symbol;
    private final String currency;
    private final LocalDate fiscalDateEnding;

    //Balance sheet
    private final long totalAssets;
    private final long totalLiabilities;
    private final long totalShareholderEquity;

    //Income statement
    private final long totalRevenue;
    private final long grossProfit;
    private final long costOfRevenue;
    private final long operatingExpenses;
    private final long ebit;
    private final long netIncome;

    //Cash Flow
    private final long operatingCashFlow;
    private final long capitalExpenditures;
    private final long cashFlowFromInvesting;
    private final long cashFlowFromFinancing;
    private final long dividendPayout;

    public FinancialStatement(
            String symbol,
            String currency,
            LocalDate fiscalDateEnding,
            long totalAssets,
            long totalLiabilities,
            long totalShareholderEquity,
            long totalRevenue,
            long grossProfit,
            long costOfRevenue,
            long operatingExpenses,
            long ebit,
            long netIncome,
            long operatingCashFlow,
            long capitalExpenditures,
            long cashFlowFromInvesting,
            long cashFlowFromFinancing,
            long dividendPayout
    ) {
        this.symbol = symbol;
        this.currency = currency;
        this.fiscalDateEnding = fiscalDateEnding;

        this.totalAssets = totalAssets;
        this.totalLiabilities = totalLiabilities;
        this.totalShareholderEquity = totalShareholderEquity;

        this.totalRevenue = totalRevenue;
        this.grossProfit = grossProfit;
        this.costOfRevenue = costOfRevenue;
        this.operatingExpenses = operatingExpenses;
        this.ebit = ebit;
        this.netIncome = netIncome;

        this.operatingCashFlow = operatingCashFlow;
        this.capitalExpenditures = capitalExpenditures;
        this.cashFlowFromInvesting = cashFlowFromInvesting;
        this.cashFlowFromFinancing = cashFlowFromFinancing;
        this.dividendPayout = dividendPayout;
    }

    public String getSymbol() { return symbol; }
    public String getCurrency() { return currency; }
    public LocalDate getFiscalDateEnding() { return fiscalDateEnding; }

    public long getTotalAssets() { return totalAssets; }
    public long getTotalLiabilities() { return totalLiabilities; }
    public long getTotalShareholderEquity() { return totalShareholderEquity; }

    public long getTotalRevenue() { return totalRevenue; }
    public long getGrossProfit() { return grossProfit; }
    public long getCostOfRevenue() { return costOfRevenue; }
    public long getOperatingExpenses() { return operatingExpenses; }
    public long getEbit() { return ebit; }
    public long getNetIncome() { return netIncome; }

    public long getOperatingCashFlow() { return operatingCashFlow; }
    public long getCapitalExpenditures() { return capitalExpenditures; }
    public long getCashFlowFromInvesting() { return cashFlowFromInvesting; }
    public long getCashFlowFromFinancing() { return cashFlowFromFinancing; }
    public long getDividendPayout() { return dividendPayout; }


}
