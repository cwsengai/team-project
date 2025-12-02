package interfaceadapter.presenter;

import java.util.ArrayList;
import java.util.List;

import entity.Company;
import frameworkanddriver.CompanyListPage;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.view_model.CompanyListViewModel;
import usecase.company_list.CompanyListOutputBoundary;
import usecase.company_list.CompanyListOutputData;

public class CompanyListPresenter implements CompanyListOutputBoundary {
    private final CompanyListPage page;
    private final CompanyListViewModel viewModel;

    public CompanyListPresenter(CompanyListPage page, CompanyListViewModel viewModel) {
        this.page = page;
        this.viewModel = viewModel;
    }

    @Override
    public void presentCompanyList(CompanyListOutputData outputData) {
        List<CompanyDisplayData> displayList = new ArrayList<>();

        for (Company company : outputData.getCompanies()) {
            String formattedCap = formatMarketCap(company.getMarketCapitalization());
            String formattedPE = formatPeRatio(company.getPeRatio());

            displayList.add(new CompanyDisplayData(
                    company.getSymbol(),
                    company.getName(),
                    company.getCountry(),
                    formattedCap,
                    formattedPE
            ));
        }

        viewModel.setCompanies(displayList);
        page.updateCompanyList(displayList);
    }

    @Override
    public void presentError(String errorMessage) {
        page.displayError(errorMessage);
    }

    private String formatMarketCap(double marketCap) {
        if (marketCap >= 1_000_000_000_000.0) {
            return String.format("$%.1fT", marketCap / 1_000_000_000_000.0);
        }
        else if (marketCap >= 1_000_000_000.0) {
            return String.format("$%.1fB", marketCap / 1_000_000_000.0);
        }
        else if (marketCap >= 1_000_000.0) {
            return String.format("$%.1fM", marketCap / 1_000_000.0);
        }
        else {
            return String.format("$%.0f", marketCap);
        }
    }

    private String formatPeRatio(double peRatio) {
        if (peRatio <= 0) return "N/A";
        return String.format("%.2f", peRatio);
    }
}
