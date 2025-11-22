package use_case.news;

import entity.NewsArticle;
import java.util.List;


public interface NewsGateway {
    List<NewsArticle> fetchArticles(String symbol);
}
