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
                "https://example.com/apple",
                now,
                "This is a summary.",
                "Reuters"
        );

        assertEquals("AAPL", article.symbol());
        assertEquals("Apple launches new product", article.title());
        assertEquals("https://example.com/apple", article.url());
        assertEquals(now, article.publishedAt());
        assertEquals("This is a summary.", article.summary());
        assertEquals("Reuters", article.source());
    }
}

