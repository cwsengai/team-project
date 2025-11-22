package use_case.company;

import entity.Company;

public interface CompanyOutputBoundary {
    void presentCompany(Company company);

    void presentError(String message);
}
