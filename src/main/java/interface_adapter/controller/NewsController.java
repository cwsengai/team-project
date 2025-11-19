package interface_adapter.controller;

import use_case.news.NewsInputBoundary;

public class NewsController {
    private final NewsInputBoundary interactor;

    public NewsController(NewsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void onNewsRequest(String symbol){
        interactor.fetchNews(symbol);
    }
}
