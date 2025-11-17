package use_case.search_company;

import entity.Company;
import java.util.List;

/**
 * Output Data for Search Company Use Case
 */

public class SearchCompanyOutputData {
    private final List<Company> matchingCompanies;
    private final boolean success;

    public SearchCompanyOutputData(List<Company> matchingCompanies, boolean success) {
        this.matchingCompanies = matchingCompanies;
        this.success = success;
    }

    public List<Company> getMatchingCompanies() {
        return matchingCompanies;
    }

    public boolean isSuccess() {
        return success;
    }
}
