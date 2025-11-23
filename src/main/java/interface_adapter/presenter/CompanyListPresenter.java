package interface_adapter.presenter;

import entity.Company;
import interface_adapter.company_list.CompanyDisplayData;
import interface_adapter.view_model.CompanyListViewModel;
import use_case.company_list.CompanyListOutputBoundary;
import use_case.company_list.CompanyListOutputData;
import java.util.ArrayList;
import java.util.List;

public class CompanyListPresenter implements CompanyListOutputBoundary {
    private final CompanyListViewModel viewModel;

    public CompanyListPresenter(CompanyListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentCompanyList(CompanyListOutputData outputData) {
        List<CompanyDisplayData> displayList = new ArrayList<>();

        for (Company company : outputData.getCompanies()) {

            // Extract data from Company entity
            String symbol = company.getSymbol();              // Note: it's getSymbol(), not getTicker()!
            String name = company.getName();
            String country = company.getCountry();
            long marketCap = (long) company.getMarketCapitalization();  // Note: it's a long, not double!
            float peRatio = company.getPeRatio();             // Note: it's a float!

            // Format the numbers for display
            String formattedCap = formatMarketCap(marketCap);
            String formattedPE = formatPeRatio(peRatio);

            // Create display data object
            displayList.add(new CompanyDisplayData(
                    symbol,
                    name,
                    country,
                    formattedCap,
                    formattedPE
            ));
        }

        viewModel.setCompanies(displayList);
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
    }

    // Format the number and ratio

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
        if (peRatio <= 0) {
            return "N/A";
        }
        return String.format("%.2f", peRatio);
    }
}
