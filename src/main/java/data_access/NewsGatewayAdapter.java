package data_access;

import entity.NewsArticle;
import use_case.NewsGateway;
import java.util.List;

/**
 * Adapter that adapts use_case.news.NewsGateway to use_case.NewsGateway interface
 */
public class NewsGatewayAdapter implements NewsGateway {
    
    private final use_case.news.NewsGateway oldNewsGateway;
    
    public NewsGatewayAdapter(use_case.news.NewsGateway oldNewsGateway) {
        this.oldNewsGateway = oldNewsGateway;
    }
    
    @Override
    public List<NewsArticle> getRelatedNews(String ticker) throws Exception {
        return oldNewsGateway.fetchArticles(ticker);
    }
}

