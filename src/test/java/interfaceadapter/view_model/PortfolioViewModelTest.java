package interfaceadapter.view_model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PortfolioViewModelTest {

    @Test
    void testPositionViewFormatting() {
        PositionView p = new PositionView(1234.5, -12.345);
        assertEquals("$1234.50", p.getFormattedMarketValue());
        assertEquals("$-12.35", p.getFormattedGain());
    }

    @Test
    void testPortfolioViewModelFormatting() {
        LocalDateTime t = LocalDateTime.of(2025, 6, 7, 8, 9, 10);
        PortfolioViewModel vm = new PortfolioViewModel(10.0, 5.0, 15.0, t);

        assertEquals("$10.00", vm.getFormattedRealizedGain());
        assertEquals("$5.00", vm.getFormattedUnrealizedGain());
        assertEquals("$15.00", vm.getFormattedTotalGain());
        assertEquals("2025-06-07 08:09:10", vm.getFormattedSnapshotTime());
    }
}
