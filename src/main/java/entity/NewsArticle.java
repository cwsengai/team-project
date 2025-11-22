package entity;

import java.time.LocalDateTime;


public class NewsArticle {
    private final String symbol;
    private final String title;
    private final String url;
    private final LocalDateTime publishedAt;
    private final String summary;
    private final String source;

    public NewsArticle(String symbol, String title, String url, LocalDateTime publishedAt, String summary, String source) {
        this.symbol = symbol;
        this.title = title;
        this.url = url;
        this.publishedAt = publishedAt;
        this.summary = summary;
        this.source = source;
    }

    public String getSymbol() { return symbol; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public String getSummary() { return summary; }
    public String getSource() { return source; }
}
