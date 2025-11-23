package use_case.news;



import entity.NewsArticle;

import java.util.List;

public class NewsInteractor implements NewsInputBoundary {
    private final NewsGateway gateway;
    private final NewsOutputBoundary presenter;

    public NewsInteractor(NewsGateway gateway,
                                        NewsOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void fetchNews(String symbol) {
        List<NewsArticle> statements = gateway.fetchArticles(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No related news found for: " + symbol);
            return;
        }

        presenter.presentNews(statements);

    }

}
