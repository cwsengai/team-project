package usecase.company_list;

import java.util.List;

import entity.Company;

/**
 * Output data for the Company List use case.
 * Holds the list of retrieved companies and the success status.
 */
public record CompanyListOutputData(List<Company> companies) {

    /**
     * Creates a new CompanyListOutputData instance.
     *
     * @param companies the list of companies retrieved by the use case
     */
    public CompanyListOutputData {
    }

    /**
     * Returns the list of companies.
     *
     * @return the list of retrieved companies
     */
    @Override
    public List<Company> companies() {
        return companies;
    }

}
