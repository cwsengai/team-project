package usecase.news;

/**
 * Input boundary for the news retrieval use case.
 * Accepts the input data necessary to trigger fetching and presenting news articles.
 */
public interface NewsInputBoundary {
    /**
     * Executes the news retrieval use case using the provided input data.
     *
     * @param data the input data containing the symbol to fetch news for
     */
    void execute(NewsInputData data);
}
