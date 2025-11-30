package dataaccess;

import entity.NewsArticle;
import usecase.NewsGateway;
import java.util.List;

/**
 * Adapter that adapts use_case.news.NewsGateway to use_case.NewsGateway interface
 */
public class NewsGatewayAdapter implements NewsGateway {
    
    private final usecase.news.NewsGateway oldNewsGateway;
    
    public NewsGatewayAdapter(usecase.news.NewsGateway oldNewsGateway) {
        this.oldNewsGateway = oldNewsGateway;
    }
    
    @Override
    public List<NewsArticle> getRelatedNews(String ticker) throws Exception {
        return oldNewsGateway.fetchArticles(ticker);
    }
}

