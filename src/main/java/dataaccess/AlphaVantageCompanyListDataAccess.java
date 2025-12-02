
package dataaccess;

import java.util.ArrayList;
import java.util.List;

import entity.Company;
import usecase.company.CompanyGateway;
import usecase.company_list.CompanyListDataAccess;

/**
 * Data access implementation for Company List use case.
 * Modified to populate the Search Data Access cache upon fetching.
 */
public class AlphaVantageCompanyListDataAccess implements CompanyListDataAccess {
    private final CompanyGateway gateway;
    private final boolean useSampleData;

    public AlphaVantageCompanyListDataAccess(CompanyGateway gateway, boolean useSampleData) {
        this.gateway = gateway;
        this.useSampleData = useSampleData;
    }

    @Override
    public List<Company> getCompanyList() {
        final List<String> tickers = useSampleData
                ? Top100Companies.getSample(20)
                : Top100Companies.getAll();

        final List<Company> companies = new ArrayList<>();

        for (String ticker : tickers) {
            try {
                final Company company = gateway.fetchOverview(ticker);
                if (company != null && company.getMarketCapitalization() > 0) {
                    companies.add(company);
                }

                // Rate limit handling
                Thread.sleep(12000);

            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while fetching company: " + ticker);
            }
            catch (Exception ex) {
                System.err.println("Error fetching company " + ticker + ": " + ex.getMessage());
            }
        }

        return companies;
    }
}
