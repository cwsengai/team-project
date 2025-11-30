package usecase.company;

/**
 * Handles the input for the company use case.
 */
public interface CompanyInputBoundary {
    /**
     * Executes the use case with the provided input data.
     *
     * @param data the input data
     */
    void execute(CompanyInputData data);
}
