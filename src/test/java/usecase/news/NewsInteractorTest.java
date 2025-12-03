package usecase.news;

import entity.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsInteractorTest {

    @Test
    void testNullListTriggersError() {
        NewsGateway fakeGateway = symbol -> null;

        StringBuilder errorCaptured = new StringBuilder();

        NewsOutputBoundary fakePresenter = new NewsOutputBoundary() {
            @Override
            public void presentNews(NewsOutputData data) {
                fail("Presenter should not be called for null list");
            }

            @Override
            public void presentError(String message) {
                errorCaptured.append(message);
            }
        };

        NewsInteractor interactor =
                new NewsInteractor(fakeGateway, fakePresenter);

        interactor.execute(new NewsInputData("AAPL"));

        assertTrue(errorCaptured.toString().contains("No related news"),
                "Expected error message for null list");
    }

    @Test
    void testEmptyListTriggersError() {
        NewsGateway fakeGateway = symbol -> List.of(); // empty list

        StringBuilder errorCaptured = new StringBuilder();

        NewsOutputBoundary fakePresenter = new NewsOutputBoundary() {
            @Override
            public void presentNews(NewsOutputData data) {
                fail("Presenter should not be called for empty list");
            }

            @Override
            public void presentError(String message) {
                errorCaptured.append(message);
            }
        };

        NewsInteractor interactor =
                new NewsInteractor(fakeGateway, fakePresenter);

        interactor.execute(new NewsInputData("AAPL"));

        assertTrue(errorCaptured.toString().contains("No related news"),
                "Expected error message for empty list");
    }

    @Test
    void testMultipleArticlesLoopBranches() {
        NewsArticle a1 = new NewsArticle(
                "AAPL",
                "Apple launches new product",
                "http://example.com/1",
                LocalDateTime.of(2024, 1, 1, 10, 0),
                "Summary 1",
                "Reuters"
        );

        NewsArticle a2 = new NewsArticle(
                "AAPL",
                "Apple expands services division",
                "http://example.com/2",
                LocalDateTime.of(2024, 1, 2, 11, 0),
                "Summary 2",
                "Bloomberg"
        );

        NewsGateway fakeGateway = symbol -> List.of(a1, a2);

        final NewsOutputData[] captured = new NewsOutputData[1];

        NewsOutputBoundary fakePresenter = new NewsOutputBoundary() {
            @Override
            public void presentNews(NewsOutputData data) {
                captured[0] = data;
            }

            @Override
            public void presentError(String message) {
                fail("Should not show error for valid news list");
            }
        };

        NewsInteractor interactor =
                new NewsInteractor(fakeGateway, fakePresenter);

        interactor.execute(new NewsInputData("AAPL"));

        assertNotNull(captured[0], "Output should be captured");
        assertEquals("AAPL", captured[0].getSymbol());
        assertEquals(2, captured[0].getStatements().size(),
                "Expected both formatted articles");
    }
}
