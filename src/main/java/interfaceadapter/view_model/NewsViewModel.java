package interfaceadapter.view_model;

public class NewsViewModel {

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
