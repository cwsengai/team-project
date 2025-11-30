package usecase;

import entity.NewsArticle;
import java.util.List;

/**
 * NewsGateway defined the size of news attained from the company。
 */
public interface NewsGateway {

    List<NewsArticle> getRelatedNews(String ticker) throws Exception;
}
