package interfaceadapter.presenter;

import java.util.ArrayList;
import java.util.List;

import entity.Company;
import frameworkanddriver.CompanyListPage;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.company_list.DataFormatters;
import interfaceadapter.view_model.SearchCompanyViewModel;
import usecase.search_company.SearchCompanyOutputBoundary;
import usecase.search_company.SearchCompanyOutputData;

/**
 * Presenter for Search Company use case.
 * Formats search results for display in the UI.
 */
public record SearchCompanyPresenter(CompanyListPage page,
                                     SearchCompanyViewModel viewModel) implements SearchCompanyOutputBoundary {

    @Override
    public void presentSearchResults(SearchCompanyOutputData outputData) {
        List<CompanyDisplayData> displayList = new ArrayList<>();

        for (Company company : outputData.companies()) {
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
                formattedCap = "-";
                formattedPE = "-";
                country = "-";
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

        // Log search results
        System.out.println("Search found " + displayList.size() + " results");
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
        page.displayError(errorMessage);
        // Log search error
        System.err.println("Search error: " + errorMessage);
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
        return DataFormatters.formatPeRatio(peRatio);
    }
}
