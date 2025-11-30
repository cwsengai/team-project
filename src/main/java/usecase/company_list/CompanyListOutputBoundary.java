package usecase.company_list;

/**
 * The output boundary for Company List Use Case
 */

public interface CompanyListOutputBoundary {
    void presentCompanyList(CompanyListOutputData outputData);
    void presentError(String errorMessage);
}
