package usecase.company_list;

/**
 * The output boundary for the Company List use case.
 * Defines how the results or errors should be presented to the user interface layer.
 */
public interface CompanyListOutputBoundary {

    /**
     * Presents the successfully loaded list of companies.
     *
     * @param outputData the output data containing company list information
     */
    void presentCompanyList(CompanyListOutputData outputData);

    /**
     * Presents an error message if the company list fails to load.
     *
     * @param errorMessage the error message describing the failure
     */
    void presentError(String errorMessage);
}
