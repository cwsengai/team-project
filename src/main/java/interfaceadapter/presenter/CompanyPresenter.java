package interfaceadapter.presenter;

import interfaceadapter.view_model.CompanyViewModel;
import usecase.company.CompanyOutputBoundary;
import usecase.company.CompanyOutputData;

public record CompanyPresenter(CompanyViewModel viewmodel) implements CompanyOutputBoundary {

    @Override
    public void presentCompany(CompanyOutputData data) {
        viewmodel.setError(null);
        viewmodel.setSymbol(data.symbol());
        viewmodel.setName(data.name());
        viewmodel.setSector(data.sector());
        viewmodel.setIndustry(data.industry());
        viewmodel.setDescription(data.description());
        viewmodel.notifyListener();
    }

    @Override
    public void presentError(String message) {
        viewmodel.setError(message);
        viewmodel.notifyListener();
    }
}
