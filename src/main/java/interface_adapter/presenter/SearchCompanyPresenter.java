package interface_adapter.presenter;

import entity.Company;
import interface_adapter.view_model.CompanyListViewModel;
import interface_adapter.view_model.SearchCompanyViewModel;
import use_case.search_company.SearchCompanyOutputBoundary;
import use_case.search_company.SearchCompanyOutputData;
import interface_adapter.company_list.CompanyDisplayData;

import java.util.ArrayList;
import java.util.List;

public class SearchCompanyPresenter implements SearchCompanyOutputBoundary {
    private final SearchCompanyViewModel viewModel;

    public SearchCompanyPresenter(SearchCompanyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSearchResults(SearchCompanyOutputData outputData){
        List<CompanyDisplayData> displayList = new ArrayList<>();

        for (Company company: outputData.getMatchingCompanies()){
            String formattedCap = formatMarketCap((long) company.getMarketCapitalization());
            String formattedPE = formatPeRatio(company.getPeRatio());

            displayList.add(new CompanyDisplayData(
                    company.getSymbol(),
                    company.getName(),
                    company.getCountry(),
                    formattedCap,
                    formattedPE
            ));
        }

        viewModel.setSearchResults(displayList);
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    private String formatMarketCap(long marketCap) {
        if (marketCap >= 1_000_000_000_000L) {
            return String.format("$%.1fT", marketCap / 1_000_000_000_000.0);
        } else if (marketCap >= 1_000_000_000L) {
            return String.format("$%.1fB", marketCap / 1_000_000_000.0);
        } else if (marketCap >= 1_000_000L) {
            return String.format("$%.1fM", marketCap / 1_000_000.0);
        } else {
            return String.format("$%d", marketCap);
        }
    }

    private String formatPeRatio(float peRatio) {
        if (peRatio <= 0) return "N/A";
        return String.format("%.2f", peRatio);
    }
}


