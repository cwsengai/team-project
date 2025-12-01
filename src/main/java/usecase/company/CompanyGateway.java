package usecase.company;

import entity.Company;

/**
 * Provides access to company-related data sources.
 */
public interface CompanyGateway {
    /**
     * Retrieves a company's overview data by its symbol.
     *
     * @param symbol the ticker symbol
     * @return the company overview
     */
    Company fetchOverview(String symbol);
}