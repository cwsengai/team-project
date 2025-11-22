package use_case.news;

import entity.FinancialStatement;
import entity.NewsArticle;

import java.util.List;

public interface NewsOutputBoundary {
    void presentNews(List<NewsArticle> newsArticles);

    void presentError(String message);
}
