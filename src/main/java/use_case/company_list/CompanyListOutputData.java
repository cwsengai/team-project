package use_case.company_list;

import entity.Company;
import java.util.List;

/**
 * Output Data for Company List use case
 */
public class CompanyListOutputData {
    private final List<Company> companies;
    private final boolean success;

    public CompanyListOutputData(List<Company> companies, boolean success) {
        this.companies = companies;
        this.success = success;
    }

    public List<Company> getCompanies() {return companies;}

    public boolean isSuccess() {return success;}
}
