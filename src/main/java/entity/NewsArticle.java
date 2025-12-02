package entity;

import java.time.LocalDateTime;

public record NewsArticle(String symbol, String title, String url, LocalDateTime publishedAt, String summary,
                          String source) {
}
