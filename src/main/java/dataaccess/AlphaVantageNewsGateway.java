package dataaccess;

import java.io.IOException;
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

public class AlphaVantageNewsGateway implements NewsGateway {
    private final Api api;

    public AlphaVantageNewsGateway(Api api) {
        this.api = api;
    }

    @Override
    public List<NewsArticle> fetchArticles(String symbol) {
        String jsonString = null;
        try {
            jsonString = api.getFuncNewsSentiment(symbol);
        }
        catch (IOException ex) {
            // handle or log as appropriate; returning null for now
            ex.printStackTrace();
        }

        final List<NewsArticle> articles = new ArrayList<>();

        if (jsonString != null) {

            final JSONObject json = new JSONObject(jsonString);

            if (json.has("feed")) {

                final JSONArray feedArray = json.getJSONArray("feed");

                for (int i = 0; i < feedArray.length(); i++) {
                    final JSONObject feedObject = feedArray.getJSONObject(i);

                    final DateTimeFormatter formatter =
                            new DateTimeFormatterBuilder()
                                    .appendPattern("yyyyMMdd'T'HHmm")
                                    .optionalStart()
                                    .appendPattern("ss")
                                    .optionalEnd()
                                    .toFormatter();

                    final String rawTime = feedObject.optString("time_published");
                    final LocalDateTime timePublished = LocalDateTime.parse(rawTime, formatter);

                    final String notavailable = "N/A";
                    final NewsArticle article = new NewsArticle(
                            symbol,
                            feedObject.optString("title", notavailable),
                            feedObject.optString("url", notavailable),
                            timePublished,
                            feedObject.optString("summary", notavailable),
                            feedObject.optString("source", notavailable)
                    );
                    articles.add(article);
                }
            }
        }

        return articles;

    }

}
