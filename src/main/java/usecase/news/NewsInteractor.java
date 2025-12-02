package usecase.news;

import java.util.ArrayList;
import java.util.List;

import entity.NewsArticle;

public record NewsInteractor(NewsGateway gateway, NewsOutputBoundary presenter) implements NewsInputBoundary {

    @Override
    public void execute(NewsInputData data) {
        final String symbol = data.symbol();
        final List<NewsArticle> statements = gateway.fetchArticles(symbol);

        if (statements == null || statements.isEmpty()) {
            presenter.presentError("No related news found for: " + symbol);
        } else {
            final List<String> formatted = new ArrayList<>();
            final String blank = "\n";
            for (NewsArticle article : statements) {
                final String block =
                        "Title: " + article.title() + blank
                                + "Source: " + article.source() + blank
                                + "Published At: " + article.publishedAt() + blank
                                + "Summary: " + article.summary() + blank
                                + "----------------------------------";
                formatted.add(block);
            }
            final NewsOutputData output = new NewsOutputData(symbol, formatted);
            presenter.presentNews(output);
        }

    }

}
