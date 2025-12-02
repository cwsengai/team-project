package entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntityLayerTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(10000.0, "user123");
    }

    // --- 1. Core Logic of Position Class (Most difficult part to cover) ---
    @Test
    void testPositionComplexLogic() {
        // 1. Initialize Open Position (Buy 10 @ 100)
        Position pos = new Position("AAPL", true, 0, 0);
        double pnl = pos.update(true, 10, 100.0);
        assertEquals(0.0, pnl);
        assertTrue(pos.isLong());
        assertEquals(10, pos.getQuantity());

        // 2. Add to Position (Buy 10 @ 120) -> Avg price becomes 110
        pos.update(true, 10, 120.0);
        assertEquals(110.0, pos.getAvgPrice());

        // 3. Reduce/Close Partial Position (Sell 5 @ 130) -> Covers "tradeQty <= quantity" branch
        pnl = pos.update(false, 5, 130.0);
        assertEquals(100.0, pnl); // (130 - 110) * 5
        assertEquals(15, pos.getQuantity());

        // 4. Flip Position (Sell 20 @ 100) -> Covers "tradeQty > quantity" branch
        // Currently Long 15 shares, Sell 20 shares -> Close 15 shares, then Short 5 shares
        pnl = pos.update(false, 20, 100.0);
        assertEquals(-150.0, pnl); // Loss (100 - 110) * 15
        assertEquals(5, pos.getQuantity());
        assertFalse(pos.isLong()); // Becomes Short

        // 5. Cover getUnrealizedPnL (Short position profit scenario)
        // Cost 100, Current 80 -> Profit 20 * 5 = 100
        assertEquals(100.0, pos.getUnrealizedPnL(80.0));

        // 6. Cover special case where quantity == 0
        Position emptyPos = new Position("TEST", true, 0, 0);
        assertEquals(0.0, emptyPos.getUnrealizedPnL(100.0));
    }

    // --- 2. Trade Execution and Statistics of Account Class ---
    @Test
    void testAccountExecution() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Normal Buy
        account.executeTrade("AAPL", true, 10, 100.0, now);
        assertEquals(9000.0, account.getBalance());

        // 2. Calculate Total Equity
        assertEquals(10000.0, account.calculateTotalEquity(100.0, "AAPL"));

        // 3. Trigger TradeClosedListener (Sell to Close)
        final boolean[] listenerTriggered = {false};
        account.addTradeClosedListener(record -> {
            listenerTriggered[0] = true;
            assertNotNull(record);
            assertEquals("AAPL", record.getTicker());
        });

        account.executeTrade("AAPL", false, 10, 120.0, now);
        assertTrue(listenerTriggered[0]);

        // 4. Cover all Statistics Getters
        assertEquals(1, account.getTotalTrades());
        assertEquals(1, account.getWinningTrades());
        assertEquals(0, account.getLosingTrades());
        assertEquals(1.0, account.getWinRate());
        assertEquals(200.0, account.getMaxGain());
        assertNotNull(account.getPositions());

        // 5. Cover some boundary calculations in Account
        assertEquals(200.0, account.getTotalProfit(10200.0));
        assertTrue(account.getTotalReturnRate(10200.0) > 0);
        assertEquals(0.0, account.getMaxDrawdown());
        // No drawdown
    }

    // --- 3. Cover Account Edge Cases (e.g., initial denominator is 0) ---
    @Test
    void testAccountEdgeCases() {
        Account zeroAcc = new Account(0.0, "u2");
        assertEquals(0.0, zeroAcc.getTotalReturnRate(0.0));
        assertEquals(0.0, zeroAcc.getWinRate());
    }

    // --- 4. Cover Getters of Simple Data Objects ---
    @Test
    void testDataObjects() {
        // SimulatedTradeRecord
        SimulatedTradeRecord r = new SimulatedTradeRecord("A", true, 1, 10.0, 11.0, 1.0, LocalDateTime.now(), LocalDateTime.now(), "u1");
        assertEquals("A", r.getTicker());
        assertTrue(r.isLong());
        assertEquals(1, r.getQuantity());
        assertEquals(10.0, r.getEntryPrice());
        assertEquals(11.0, r.getExitPrice());
        assertEquals(1.0, r.getRealizedPnL());
        assertNotNull(r.getEntryTime());
        assertNotNull(r.getExitTime());
        assertEquals("u1", r.getUserId());
        assertNotNull(r.toString());

        // SimulatedOrder (Even if not heavily used, must cover for entity layer)
        SimulatedOrder o = new SimulatedOrder("id");
        o.setFilled(true);
        o.setEntryPrice(10.0);
        assertTrue(o.isFilled());
        assertEquals(10.0, o.getEntryPrice());
        assertEquals("id", o.getOrderId());
    }
}