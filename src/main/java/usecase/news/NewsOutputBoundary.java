package usecase.news;

/**
 * Output boundary for the news retrieval use case.
 * Implementations handle presenting formatted news data
 * or displaying an appropriate error message.
 */
public interface NewsOutputBoundary {
    /**
     * Presents the retrieved and formatted news data.
     *
     * @param data the processed news data to be displayed
     */
    void presentNews(NewsOutputData data);

    /**
     * Presents an error message when news retrieval fails.
     *
     * @param message the descriptive error message
     */
    void presentError(String message);
}
