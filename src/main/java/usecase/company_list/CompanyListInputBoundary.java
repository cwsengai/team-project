package usecase.company_list;

/**
 * Interface for the Company List Input Boundary.
 * Defines the use case for retrieving a list of companies.
 */
public interface CompanyListInputBoundary {

    /**
     * Executes the Company List use case using the provided input data.
     *
     * @param inputData the input data for requesting the company list
     */
    void execute(CompanyListInputData inputData);
}
