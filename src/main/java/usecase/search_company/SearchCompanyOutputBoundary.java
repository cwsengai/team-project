package usecase.search_company;

/**
 * Output boundary for the Search Company use case.
 * Defines methods to present search results or error messages.
 */
public interface SearchCompanyOutputBoundary {

    /**
     * Presents the successful search results.
     *
     * @param outputData the data containing search results
     */
    void presentSearchResults(SearchCompanyOutputData outputData);

    /**
     * Presents an error message when the search fails.
     *
     * @param errorMessage the error message to display
     */
    void presentError(String errorMessage);
}
