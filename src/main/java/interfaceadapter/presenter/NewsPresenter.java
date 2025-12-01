package interfaceadapter.presenter;

import interfaceadapter.view_model.NewsViewModel;
import usecase.news.NewsOutputBoundary;
import usecase.news.NewsOutputData;

public class NewsPresenter implements NewsOutputBoundary {

    private final NewsViewModel viewmodel;

    public NewsPresenter(NewsViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void presentNews(NewsOutputData data) {
        viewmodel.error = null;
        viewmodel.articles = data.getStatements();
        viewmodel.formattedNews = String.join("\n", data.getStatements());
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.articles = null;
        viewmodel.formattedNews = "";
        viewmodel.error = message;
        viewmodel.notifyListener();
    }

}


