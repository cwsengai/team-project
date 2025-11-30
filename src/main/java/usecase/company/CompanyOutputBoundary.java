package usecase.company;

import entity.Company;

public interface CompanyOutputBoundary {
    void presentCompany(Company company);

    void presentError(String message);
}
