package usecase.search_company;

import java.util.List;

import entity.Company;

/**
 * Output data for Search Company use case.
 */
public class SearchCompanyOutputData {
    private final List<Company> companies;
    private final boolean success;

    public SearchCompanyOutputData(List<Company> companies, boolean success) {
        this.companies = companies;
        this.success = success;
    }

    public List<Company> getCompanies() {
        return companies;
    }

}
