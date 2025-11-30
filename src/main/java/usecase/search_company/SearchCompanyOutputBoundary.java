package usecase.search_company;

/**
 * Output Boundary for Search Company Use Case
 */
public interface SearchCompanyOutputBoundary {
    void presentSearchResults(SearchCompanyOutputData outputData);
    void presentError(String errorMessage);
}
