package usecase.search_company;

/**
 * Input boundary for the Search Company use case.
 * Defines the method required to initiate a company search operation.
 */
public interface SearchCompanyInputBoundary {

    /**
     * Executes the search company use case using the provided input data.
     *
     * @param inputData the input data containing search keywords and filters
     */
    void execute(SearchCompanyInputData inputData);
}
