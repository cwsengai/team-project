package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NewsArticleTest {

    @Test
    void testConstructorAndGetters() {

        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 30);

        NewsArticle article = new NewsArticle(
                "AAPL",
                "Apple launches new product",
                "http://example.com/apple",
                now,
                "This is a summary.",
                "Reuters"
        );

        assertEquals("AAPL", article.getSymbol());
        assertEquals("Apple launches new product", article.getTitle());
        assertEquals("http://example.com/apple", article.getUrl());
        assertEquals(now, article.getPublishedAt());
        assertEquals("This is a summary.", article.getSummary());
        assertEquals("Reuters", article.getSource());
    }
}

