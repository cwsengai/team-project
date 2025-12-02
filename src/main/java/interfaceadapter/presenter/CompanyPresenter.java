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
        viewmodel.setError(null);
        viewmodel.setSymbol(data.getSymbol());
        viewmodel.setName(data.getName());
        viewmodel.setSector(data.getSector());
        viewmodel.setIndustry(data.getIndustry());
        viewmodel.setDescription(data.getDescription());
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.setError(message);
        viewmodel.notifyListener();
    }
}
