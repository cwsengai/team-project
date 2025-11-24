package interface_adapter.presenter;

import entity.Company;
import interface_adapter.company_list.CompanyDisplayData;
import interface_adapter.view_model.SearchCompanyViewModel;
import use_case.search_company.SearchCompanyOutputBoundary;
import use_case.search_company.SearchCompanyOutputData;
import framework_and_driver.CompanyListPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Search Company use case.
 * Formats search results for display in the UI.
 */
public class SearchCompanyPresenter implements SearchCompanyOutputBoundary {
    private final CompanyListPage page;
    private final SearchCompanyViewModel viewModel;

    public SearchCompanyPresenter(CompanyListPage page, SearchCompanyViewModel viewModel) {
        this.page = page;
        this.viewModel = viewModel;
    }

    @Override
    public void presentSearchResults(SearchCompanyOutputData outputData) {
        List<CompanyDisplayData> displayList = new ArrayList<>();

        for (Company company : outputData.getCompanies()) {
            // ✅ Handle companies with no data (market cap = 0)
            String formattedCap;
            String formattedPE;
            String country;

            if (company.getMarketCapitalization() > 0) {
                // Company has full data
                formattedCap = formatMarketCap(company.getMarketCapitalization());
                formattedPE = formatPeRatio(company.getPeRatio());
                country = company.getCountry();
            } else {
                // Company has minimal data (just ticker)
                formattedCap = "—";
                formattedPE = "—";
                country = "—";
            }

            displayList.add(new CompanyDisplayData(
                    company.getSymbol(),
                    company.getName(),
                    country,
                    formattedCap,
                    formattedPE
            ));
        }

        viewModel.setSearchResults(displayList);
        page.updateTable(displayList);

        System.out.println("✅ Search found " + displayList.size() + " results");  // ✅ Added log
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
        page.displayError(errorMessage);
        System.err.println("❌ Search error: " + errorMessage);  // ✅ Added log
    }

    private String formatMarketCap(double marketCap) {
        if (marketCap >= 1_000_000_000_000.0) {
            return String.format("$%.1fT", marketCap / 1_000_000_000_000.0);
        } else if (marketCap >= 1_000_000_000.0) {
            return String.format("$%.1fB", marketCap / 1_000_000_000.0);
        } else if (marketCap >= 1_000_000.0) {
            return String.format("$%.1fM", marketCap / 1_000_000.0);
        } else {
            return String.format("$%.0f", marketCap);
        }
    }

    private String formatPeRatio(double peRatio) {
        if (peRatio <= 0) return "N/A";
        return String.format("%.2f", peRatio);
    }
}