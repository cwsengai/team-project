package use_case.company_list;

import entity.Company;
import java.util.List;

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
