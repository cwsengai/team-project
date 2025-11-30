package interfaceadapter.presenter;

import interfaceadapter.view_model.CompanyViewModel;
import usecase.company.CompanyOutputBoundary;
import usecase.company.CompanyOutputData;

public class CompanyPresenter implements CompanyOutputBoundary {

    private final CompanyViewModel viewmodel;

    public CompanyPresenter(CompanyViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void presentCompany(CompanyOutputData data) {
        viewmodel.error = null;
        viewmodel.symbol = data.getSymbol();
        viewmodel.name = data.getName();
        viewmodel.sector = data.getSector();
        viewmodel.industry = data.getIndustry();
        viewmodel.description = data.getDescription();
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.error = message;
        viewmodel.notifyListener();
    }
}