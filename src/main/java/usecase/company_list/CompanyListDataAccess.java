package usecase.company_list;

import java.util.List;

import entity.Company;

/**
 * Data access interface for Company List use case.
 * This defines what data operations the use case needs.
 */

public interface CompanyListDataAccess {
    /**
     * Retrieve top 100 companies by market cap.
     * @return List of Company entities
     */
    List<Company> getCompanyList();
}
