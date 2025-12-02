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

        assertEquals("AAPL", fs.symbol());
        assertEquals("USD", fs.currency());
        assertEquals(date, fs.fiscalDateEnding());

        assertEquals(1L, fs.totalAssets());
        assertEquals(2L, fs.totalLiabilities());
        assertEquals(3L, fs.totalShareholderEquity());

        assertEquals(4L, fs.totalRevenue());
        assertEquals(5L, fs.grossProfit());
        assertEquals(6L, fs.costOfRevenue());
        assertEquals(7L, fs.operatingExpenses());
        assertEquals(8L, fs.ebit());
        assertEquals(9L, fs.netIncome());

        assertEquals(10L, fs.operatingCashFlow());
        assertEquals(11L, fs.capitalExpenditures());
        assertEquals(12L, fs.cashFlowFromInvesting());
        assertEquals(13L, fs.cashFlowFromFinancing());
        assertEquals(14L, fs.dividendPayout());
    }
}


