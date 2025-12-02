package entity;

import java.time.LocalDate;

/**
 * @param totalAssets       Balance sheet
 * @param totalRevenue      Income statement
 * @param operatingCashFlow Cash Flow
 */
public record FinancialStatement(String symbol, String currency, LocalDate fiscalDateEnding, long totalAssets,
                                 long totalLiabilities, long totalShareholderEquity, long totalRevenue,
                                 long grossProfit, long costOfRevenue, long operatingExpenses, long ebit,
                                 long netIncome, long operatingCashFlow, long capitalExpenditures,
                                 long cashFlowFromInvesting, long cashFlowFromFinancing, long dividendPayout) {

}
