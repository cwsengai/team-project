package frameworkanddriver;

import entity.ChartViewModel;

public class ChartViewAdapter extends ChartWindow {

    private final CompanyPage companyPage;

    public ChartViewAdapter(CompanyPage companyPage) {
        this.companyPage = companyPage;
    }

    @Override
    public void updateChart(ChartViewModel vm) {
        companyPage.updateChart(vm);
    }

    @Override
    public void displayError(String message) {
        companyPage.displayError(message);
    }
}

