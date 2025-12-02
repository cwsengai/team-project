package usecase.search_company;

import java.util.List;

import entity.Company;

/**
 * Output data for Search Company use case.
 */
public class SearchCompanyOutputData {
    private final List<Company> companies;

    public SearchCompanyOutputData(List<Company> companies) {
        this.companies = companies;
    }

    public List<Company> getCompanies() {
        return companies;
    }

}
