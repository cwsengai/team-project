package usecase.search_company;

import entity.Company;
import java.util.List;

public interface SearchCompanyDataAccess {
    /**
     * Search for companies by name or ticker symbol.
     * @param query Search query string
     * @return List of matching Company entities
     */
    List<Company> searchCompanies(String query);
}
