package interfaceadapter.controller;

import usecase.news.NewsInputBoundary;
import usecase.news.NewsInputData;

/**
 * Controller responsible for handling news retrieval requests.
 * Receives user actions from the interface layer and forwards them
 * to the news retrieval use case interactor.
 */
public record NewsController(NewsInputBoundary interactor) {
    /**
     * Constructs a NewsController with the given interactor.
     *
     * @param interactor the input boundary for the news retrieval use case
     */
    public NewsController {
    }

    /**
     * Initiates a news retrieval request for the given symbol.
     *
     * @param symbol the stock ticker symbol to fetch news for
     */
    public void onNewsRequest(String symbol) {
        final NewsInputData data = new NewsInputData(symbol);
        interactor.execute(data);
    }
}

