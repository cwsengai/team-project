package use_case.company;

/**
 * Defines how company data should be presented to the user interface layer.
 */
public interface CompanyOutputBoundary {
    /**
     * Presents the company data.
     *
     * @param data the output data for the company
     */
    void presentCompany(CompanyOutputData data);

    /**
     * Presents an error message.
     *
     * @param message the error details
     */
    void presentError(String message);
}
