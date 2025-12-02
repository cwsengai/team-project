package interfaceadapter.presenter;

import interfaceadapter.view_model.NewsViewModel;
import usecase.news.NewsOutputBoundary;
import usecase.news.NewsOutputData;

public record NewsPresenter(NewsViewModel viewmodel) implements NewsOutputBoundary {

    @Override
    public void presentNews(NewsOutputData data) {
        viewmodel.setError(null);
        viewmodel.setFormattedNews(String.join("\n", data.statements()));
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.setFormattedNews("");
        viewmodel.setError(message);
        viewmodel.notifyListener();
    }

}
