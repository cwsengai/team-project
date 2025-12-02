package usecase.company_list;

import java.util.List;

import entity.Company;

/**
 * Output data for the Company List use case.
 * Holds the list of retrieved companies and the success status.
 */
public class CompanyListOutputData {

    private final List<Company> companies;

    /**
     * Creates a new CompanyListOutputData instance.
     *
     * @param companies the list of companies retrieved by the use case
     */
    public CompanyListOutputData(List<Company> companies) {
        this.companies = companies;
    }

    /**
     * Returns the list of companies.
     *
     * @return the list of retrieved companies
     */
    public List<Company> getCompanies() {
        return companies;
    }

}
