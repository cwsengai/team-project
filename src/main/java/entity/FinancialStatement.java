package entity;

import java.time.LocalDate;

/**
 * Represents a financial statement of a company.
 * @param symbol            the stock ticker symbol
 * @param currency          the currency of the financial figures
 * @param fiscalDateEnding  the fiscal date ending of the statement
 * @param totalAssets       the total assets value
 * @param totalLiabilities  the total liabilities value
 * @param totalShareholderEquity the total shareholder equity value
 * @param totalRevenue      the total revenue value
 * @param grossProfit       the gross profit value
 * @param costOfRevenue     the cost of revenue value
 * @param operatingExpenses the operating expenses value
 * @param ebit              the earnings before interest and taxes value
 * @param netIncome         the net income value
 * @param operatingCashFlow the operating cash flow value
 * @param capitalExpenditures the capital expenditures value
 * @param cashFlowFromInvesting the cash flow from investing activities value
 * @param cashFlowFromFinancing the cash flow from financing activities value
 * @param dividendPayout    the dividend payout value
 */
public record FinancialStatement(String symbol, String currency, LocalDate fiscalDateEnding, long totalAssets,
                                 long totalLiabilities, long totalShareholderEquity, long totalRevenue,
                                 long grossProfit, long costOfRevenue, long operatingExpenses, long ebit,
                                 long netIncome, long operatingCashFlow, long capitalExpenditures,
                                 long cashFlowFromInvesting, long cashFlowFromFinancing, long dividendPayout) {

}
