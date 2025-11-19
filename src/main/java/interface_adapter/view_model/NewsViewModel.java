package interface_adapter.view_model;

import entity.NewsArticle;

import java.util.List;

public class NewsViewModel {

    public List<NewsArticle> articles;

    public String formattedNews;

    public String error;

    private Runnable listener;

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public void notifyListener() {
        if (listener != null) listener.run();
    }
}
