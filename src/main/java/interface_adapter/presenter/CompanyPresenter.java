package interface_adapter.presenter;

import use_case.company.CompanyOutputBoundary;
import entity.Company;
import interface_adapter.view_model.CompanyViewModel;

public class CompanyPresenter implements CompanyOutputBoundary {

    private final CompanyViewModel vm;

    public CompanyPresenter(CompanyViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void presentCompany(Company overview) {
        vm.error = null;
        vm.name = overview.getName();
        vm.symbol = overview.getSymbol();
        vm.sector = overview.getSector();
        vm.industry = overview.getIndustry();
        vm.description = overview.getDescription();

        // Notify UI via the listener set by CompanyPage
        vm.notifyListener();
    }

    @Override
    public void presentError(String message) {
        vm.error = message;

        // Notify UI via listener
        vm.notifyListener();
    }
}

