package usecase.search_company;

import java.util.List;

import entity.Company;

public interface SearchCompanyDataAccess {
    /**
     * Search for companies by name or ticker symbol.
     * @param query Search query string
     * @return List of matching Company entities
     */
    List<Company> searchCompanies(String query);
}
