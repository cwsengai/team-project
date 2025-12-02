package usecase.news;

import entity.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsInteractorTest {

    @Test
    void testNoNewsTriggersError() {
        // Fake gateway returns null
        NewsGateway fakeGateway = symbol -> null;

        StringBuilder errorCaptured = new StringBuilder();

        NewsOutputBoundary fakePresenter = new NewsOutputBoundary() {
            @Override
            public void presentNews(NewsOutputData data) {
                fail("Should not present news when none exist");
            }

            @Override
            public void presentError(String message) {
                errorCaptured.append(message);
            }
        };

        NewsInteractor interactor = new NewsInteractor(fakeGateway, fakePresenter);
        interactor.execute(new NewsInputData("FAKE"));

        assertTrue(errorCaptured.toString().contains("No related news found"),
                "Expected error message when no news exists");
    }

    @Test
    void testValidNewsTriggersPresenter() {
        // A fake NewsArticle entity
        NewsArticle a = new NewsArticle(
                "TSLA",
                "Tesla launches new model",
                "https://example.com",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                "Some summary",
                "Reuters"
        );

        NewsGateway fakeGateway = symbol -> List.of(a);

        final NewsOutputData[] captured = new NewsOutputData[1];

        NewsOutputBoundary fakePresenter = new NewsOutputBoundary() {
            @Override
            public void presentNews(NewsOutputData data) {
                captured[0] = data;
            }

            @Override
            public void presentError(String message) {
                fail("Should not present error for valid news list");
            }
        };

        NewsInteractor interactor = new NewsInteractor(fakeGateway, fakePresenter);
        interactor.execute(new NewsInputData("TSLA"));

        assertNotNull(captured[0], "Presenter should receive output data");
        assertEquals("TSLA", captured[0].symbol());
        assertFalse(captured[0].statements().isEmpty());
    }
}


