package usecase.news;

import java.util.List;

import entity.NewsArticle;

/**
 * Gateway interface for retrieving news articles related to a company.
 * Implementations provide access to external data sources such as APIs.
 */
public interface NewsGateway {
    /**
     * Fetches a list of news articles associated with the given stock symbol.
     *
     * @param symbol the stock ticker symbol to retrieve news for
     * @return a list of related news articles, or an empty list if none are found
     */
    List<NewsArticle> fetchArticles(String symbol);
}
