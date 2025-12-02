package dataaccess;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Api;
import entity.NewsArticle;
import usecase.news.NewsGateway;

public record AlphaVantageNewsGateway(Api api) implements NewsGateway {

    @Override
    public List<NewsArticle> fetchArticles(String symbol) {
        String jsonString;
        try {
            jsonString = api.getFuncNewsSentiment(symbol);
        } catch (Exception ex) {
            // handle or log as appropriate; returning null for now
            System.err.println("AlphaVantageNewsGateway.fetchArticles error: " + ex.getMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                System.err.println("    at " + ste.toString());
            }
            return null;
        }

        if (jsonString == null) {
            return null;
        }

        JSONObject json = new JSONObject(jsonString);

        if (!json.has("feed")) {
            // no news
            return List.of();
        }

        JSONArray feedArray = json.getJSONArray("feed");
        List<NewsArticle> articles = new ArrayList<>();

        for (int i = 0; i < feedArray.length(); i++) {
            JSONObject feedObject = feedArray.getJSONObject(i);

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyyMMdd'T'HHmm")
                    .optionalStart()
                    .appendPattern("ss")
                    .optionalEnd()
                    .toFormatter();

            String rawTime = feedObject.optString("time_published");
            LocalDateTime timePublished = LocalDateTime.parse(rawTime, formatter);

            NewsArticle article = new NewsArticle(
                    symbol,
                    feedObject.optString("title", "N/A"),
                    feedObject.optString("url", "N/A"),
                    timePublished,
                    feedObject.optString("summary", "N/A"),
                    feedObject.optString("source", "N/A"));
            articles.add(article);
        }

        return articles;
    }

}
