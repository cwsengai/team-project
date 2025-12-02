package usecase.price_chart;

import entity.PricePoint;
import entity.TimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GetPriceByIntervalInteractorTest {

    /**
     * Test scenario 1: Successfully retrieve data.
     * Expected result: Presenter's presentPriceHistory is called.
     */
    @Test
    void loadPriceHistory_Success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PriceDataAccessInterface successGateway = (ticker, interval) -> {
            List<PricePoint> points = new ArrayList<>();
            points.add(new PricePoint(LocalDateTime.now(),
                    100.0, 110.0, 90.0, 105.0));
            return points;
        };

        TestPricePresenter mockPresenter = new TestPricePresenter(latch);

        GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(successGateway, mockPresenter);

        interactor.loadPriceHistory("AAPL", TimeInterval.DAILY);

        boolean finished = latch.await(2, TimeUnit.SECONDS);
        assertTrue(finished, "Test timed out - presenter was not called");

        assertNull(mockPresenter.capturedError, "Should not return error on success");
        assertNotNull(mockPresenter.capturedData, "Data should be presented");
        assertEquals(1, mockPresenter.capturedData.size());
        assertEquals(105.0, mockPresenter.capturedData.get(0).getClose());
    }

    /**
     * Test scenario 2: Gateway throws exception (e.g., API is down).
     * Expected result: Presenter's presentError is called.
     */
    @Test
    void loadPriceHistory_Failure_Exception() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PriceDataAccessInterface failGateway = (ticker, interval) -> {
            throw new RuntimeException("API Connection Failed");
        };

        TestPricePresenter mockPresenter = new TestPricePresenter(latch);
        GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(failGateway, mockPresenter);

        interactor.loadPriceHistory("IBM", TimeInterval.WEEKLY);

        boolean finished = latch.await(2, TimeUnit.SECONDS);
        assertTrue(finished, "Test timed out");

        assertNotNull(mockPresenter.capturedError);
        assertTrue(mockPresenter.capturedError.contains("API Connection Failed"));
        assertNull(mockPresenter.capturedData);
    }

    /**
     * Test scenario 3: Gateway returns empty data (e.g., no data for the stock).
     * Expected result: Presenter's presentError is called.
     */
    @Test
    void loadPriceHistory_Failure_EmptyData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PriceDataAccessInterface emptyGateway = (ticker, interval) -> Collections.emptyList();

        TestPricePresenter mockPresenter = new TestPricePresenter(latch);
        GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(emptyGateway, mockPresenter);

        interactor.loadPriceHistory("UNKNOWN", TimeInterval.DAILY);

        boolean finished = latch.await(2, TimeUnit.SECONDS);
        assertTrue(finished, "Test timed out");

        assertNotNull(mockPresenter.capturedError);
        assertTrue(mockPresenter.capturedError.contains("not found"));
    }

    static class TestPricePresenter implements PriceChartOutputBoundary {
        List<PricePoint> capturedData;
        String capturedError;
        private final CountDownLatch latch;

        public TestPricePresenter(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval) {
            this.capturedData = priceData;
            latch.countDown();
        }

        @Override
        public void presentError(String message) {
            this.capturedError = message;
            latch.countDown();
        }
    }
    /**
     * Test scenario 4: Gateway return null
     * expectation: Presenter's presentError be called
     */
    @Test
    void loadPriceHistory_Failure_NullData() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        PriceDataAccessInterface nullGateway = (ticker, interval) -> null;

        TestPricePresenter mockPresenter = new TestPricePresenter(latch);
        GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(nullGateway, mockPresenter);

        interactor.loadPriceHistory("NULL_STOCK", TimeInterval.DAILY);

        boolean finished = latch.await(2, TimeUnit.SECONDS);
        assertTrue(finished, "Test timed out");

        assertNotNull(mockPresenter.capturedError);
        assertTrue(mockPresenter.capturedError.contains("not found"));
    }
}
