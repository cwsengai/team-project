package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FinancialStatementTest {

    @Test
    void testConstructorAndGetters() {

        LocalDate date = LocalDate.of(2023, 12, 31);

        FinancialStatement fs = new FinancialStatement(
                "AAPL",
                "USD",
                date,
                1L,      // totalAssets
                2L,      // totalLiabilities
                3L,      // totalShareholderEquity
                4L,      // totalRevenue
                5L,      // grossProfit
                6L,      // costOfRevenue
                7L,      // operatingExpenses
                8L,      // ebit
                9L,      // netIncome
                10L,     // operatingCashFlow
                11L,     // capitalExpenditures
                12L,     // cashFlowFromInvesting
                13L,     // cashFlowFromFinancing
                14L      // dividendPayout
        );

        assertEquals("AAPL", fs.getSymbol());
        assertEquals("USD", fs.getCurrency());
        assertEquals(date, fs.getFiscalDateEnding());

        assertEquals(1L, fs.getTotalAssets());
        assertEquals(2L, fs.getTotalLiabilities());
        assertEquals(3L, fs.getTotalShareholderEquity());

        assertEquals(4L, fs.getTotalRevenue());
        assertEquals(5L, fs.getGrossProfit());
        assertEquals(6L, fs.getCostOfRevenue());
        assertEquals(7L, fs.getOperatingExpenses());
        assertEquals(8L, fs.getEbit());
        assertEquals(9L, fs.getNetIncome());

        assertEquals(10L, fs.getOperatingCashFlow());
        assertEquals(11L, fs.getCapitalExpenditures());
        assertEquals(12L, fs.getCashFlowFromInvesting());
        assertEquals(13L, fs.getCashFlowFromFinancing());
        assertEquals(14L, fs.getDividendPayout());
    }
}

