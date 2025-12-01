
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

    public AlphaVantageCompanyListDataAccess(CompanyGateway gateway) {
        this(gateway, true);
    }

    public AlphaVantageCompanyListDataAccess(CompanyGateway gateway, boolean useSampleData) {
        this.gateway = gateway;
        this.useSampleData = useSampleData;
    }

    @Override
    public List<Company> getCompanyList() {
        List<String> tickers = useSampleData
                ? Top100Companies.getSample(20)
                : Top100Companies.getAll();

        List<Company> companies = new ArrayList<>();

        for (String ticker : tickers) {
            try {
                Company company = gateway.fetchOverview(ticker);
                if (company != null && company.getMarketCapitalization() > 0) {
                    companies.add(company);
                }

                // Rate limit handling
                if (useSampleData) {
                    Thread.sleep(12000);
                } else {
                    Thread.sleep(12000);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while fetching company: " + ticker);
            } catch (Exception e) {
                System.err.println("Error fetching company " + ticker + ": " + e.getMessage());
            }
        }

        ;
        // -------------------------------------------------------------

        return companies;
    }
}