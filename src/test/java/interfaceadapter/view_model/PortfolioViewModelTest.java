package interfaceadapter.view_model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PortfolioViewModelTest {

    @Test
    void testPositionViewFormatting() {
        PositionView p = new PositionView("AAPL", 10, 100.0, 123.45, 1234.5, -12.345);
        assertEquals("$1234.50", p.getFormattedMarketValue());
        assertEquals("$-12.35", p.getFormattedGain());
    }

    @Test
    void testPortfolioViewModelFormatting() {
        LocalDateTime t = LocalDateTime.of(2025, 6, 7, 8, 9, 10);
        PositionView[] positions = new PositionView[] { new PositionView("A", 1, 1.0, 2.0, 2.0, 1.0) };
        PortfolioViewModel vm = new PortfolioViewModel("pid", positions, 10.0, 5.0, 15.0, t);

        assertEquals("$10.00", vm.getFormattedRealizedGain());
        assertEquals("$5.00", vm.getFormattedUnrealizedGain());
        assertEquals("$15.00", vm.getFormattedTotalGain());
        assertEquals("2025-06-07 08:09:10", vm.getFormattedSnapshotTime());
    }
}
