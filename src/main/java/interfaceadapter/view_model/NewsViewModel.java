package interfaceadapter.view_model;

import java.util.List;

public class NewsViewModel {

    private List<String> articles;

    private String formattedNews;

    private String error;

    private Runnable listener;

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    /**
     * Notifies the listener of property changes.
     */
    public void notifyListener() {
        if (listener != null) {
            listener.run();
        }
    }

    public void setArticles(List<String> articles) {
        this.articles = articles;
    }

    public String getFormattedNews() {
        return formattedNews;
    }

    public void setFormattedNews(String formattedNews) {
        this.formattedNews = formattedNews;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
