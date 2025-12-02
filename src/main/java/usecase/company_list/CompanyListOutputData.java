package usecase.company_list;

import java.util.List;

import entity.Company;

/**
 * Output data for the Company List use case.
 * Holds the list of retrieved companies and the success status.
 */
public class CompanyListOutputData {

    private final List<Company> companies;
    private final boolean success;

    /**
     * Creates a new CompanyListOutputData instance.
     *
     * @param companies the list of companies retrieved by the use case
     * @param success whether the retrieval operation was successful
     */
    public CompanyListOutputData(List<Company> companies, boolean success) {
        this.companies = companies;
        this.success = success;
    }

    /**
     * Returns the list of companies.
     *
     * @return the list of retrieved companies
     */
    public List<Company> getCompanies() {
        return companies;
    }

    /**
     * Returns whether the retrieval operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
}
