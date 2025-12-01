package dataaccess;

import java.util.List;

import api.Api;
import entity.NewsArticle;

public class TestNewsGateway {
    /**
     * Demonstration entry point for testing the AlphaVantageNewsGateway.
     * Initializes the API client using a demo key, retrieves news articles
     * for a sample ticker symbol, and prints basic information about the
     * returned results.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // replace "YOUR_KEY" with a real key
        Api api = new Api("demo");
        AlphaVantageNewsGateway gateway = new AlphaVantageNewsGateway(api);

        System.out.println("Fetching news for AAPL...");
        List<NewsArticle> articles = gateway.fetchArticles("AAPL");

        if (articles == null) {
            System.out.println("Gateway returned null (API error?)");
            return;
        }

        if (articles.isEmpty()) {
            System.out.println("No news found.");
            return;
        }

        System.out.println("\n--- NEWS RESULTS ---");
        for (NewsArticle a : articles) {
            System.out.println("Title: " + a.getTitle());
            System.out.println("URL: " + a.getUrl());
            System.out.println("Time: " + a.getPublishedAt());
            System.out.println("Source: " + a.getSource());
            System.out.println("Summary: " + a.getSummary());
            System.out.println("---------------------------");
        }
    }
}

