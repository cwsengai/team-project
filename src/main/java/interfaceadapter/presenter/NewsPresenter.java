package interfaceadapter.presenter;

import entity.NewsArticle;
import interfaceadapter.view_model.NewsViewModel;
import usecase.news.NewsOutputBoundary;

import java.util.List;

public class NewsPresenter implements NewsOutputBoundary {

    private final NewsViewModel vm;

    public NewsPresenter(NewsViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void presentNews(List<NewsArticle> list) {
        vm.error = null;
        vm.articles = list;
        vm.formattedNews = format(list);
        vm.notifyListener();
    }

    @Override
    public void presentError(String message) {
        vm.articles = null;
        vm.formattedNews = "";
        vm.error = message;
        vm.notifyListener();
    }

    private String format(List<NewsArticle> list) {
        StringBuilder sb = new StringBuilder();

        for (NewsArticle article : list) {
            sb.append(article.getTitle()).append("\n");
            sb.append("Source: ").append(article.getSource()).append("\n");
            sb.append("Published: ").append(article.getPublishedAt()).append("\n");
            sb.append(article.getSummary()).append("\n");
            sb.append("----------------------------------\n");
        }

        return sb.toString();
    }
}
