package usecase.news;

import java.util.ArrayList;
import java.util.List;

import entity.NewsArticle;

public class NewsInteractor implements NewsInputBoundary {
    private final NewsGateway gateway;
    private final NewsOutputBoundary presenter;

    public NewsInteractor(NewsGateway gateway,
                                        NewsOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(NewsInputData data) {
        final String symbol = data.getSymbol();
        final List<NewsArticle> statements = gateway.fetchArticles(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No related news found for: " + symbol);
        }
        else {
            final List<String> formatted = new ArrayList<>();
            final String blank = "\n";
            for (NewsArticle article : statements) {
                final String block =
                        "Title: " + article.getTitle() + blank
                                + "Source: " + article.getSource() + blank
                                + "Published At: " + article.getPublishedAt() + blank
                                + "Summary: " + article.getSummary() + blank
                                + "----------------------------------";
                formatted.add(block);
            }
            final NewsOutputData output = new NewsOutputData(symbol, formatted);
            presenter.presentNews(output);
        }

    }

}
