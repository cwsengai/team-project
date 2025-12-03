package interfaceadapter.price_chart;

import java.util.List;

import entity.Company;
import entity.FinancialStatement;
import entity.NewsArticle;

public interface CompanyDetailOutputBoundary {

    /**
     * Call this method after successfully attain the company data.
     *
     * @param companyOverview central company data
     * @param financials financial data
     * @param news news related
     */
    void presentCompanyDetail(Company companyOverview, FinancialStatement financials, List<NewsArticle> news);

    /**
     * When data fail to attain call this method to get error message.
     *
     * @param message error message
     */
    void presentError(String message);
}
