package usecase.news;

import java.util.List;

import entity.NewsArticle;

public interface NewsOutputBoundary {
    void presentNews(List<NewsArticle> newsArticles);

    void presentError(String message);
}
