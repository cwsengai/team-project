package usecase;

import entity.*;
import java.util.List;

public class ViewCompanyDetailInteractor implements CompanyDetailInputBoundary {

    private final CompanyGateway companyGateway;
    private final FinancialGateway financialGateway;
    private final NewsGateway newsGateway;
    private final CompanyDetailOutputBoundary detailPresenter;

    public ViewCompanyDetailInteractor(CompanyGateway companyGateway,
                                       FinancialGateway financialGateway,
                                       NewsGateway newsGateway,
                                       CompanyDetailOutputBoundary detailPresenter) {
        this.companyGateway = companyGateway;
        this.financialGateway = financialGateway;
        this.newsGateway = newsGateway;
        this.detailPresenter = detailPresenter;
    }

    @Override
    public void getCompanyDetail(String ticker) {
        Company companyOverview = null;
        FinancialStatement financials = null;
        List<NewsArticle> news = null;

        try {
            companyOverview = companyGateway.getCompanyOverview(ticker);

            financials = financialGateway.getFinancials(ticker, "BALANCE_SHEET");

            news = newsGateway.getRelatedNews(ticker);

        } catch (Exception e) {
            System.err.println("part of data fail to attain: " + e.getMessage());
        }

        if (companyOverview != null) {
            detailPresenter.presentCompanyDetail(companyOverview, financials, news);
        } else {
            detailPresenter.presentError("can not attain" + ticker + " 's central content");
        }
    }
}
