package data_access;
import api.Api;
import data_access.AlphaVantageNewsGateway;
import entity.NewsArticle;

import java.util.List;

public class TestNewsGateway {
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

