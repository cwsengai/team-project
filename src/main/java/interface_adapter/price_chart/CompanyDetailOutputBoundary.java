package interface_adapter.price_chart;

import entity.Company;
import entity.FinancialStatement;
import entity.NewsArticle;
import java.util.List;

public interface CompanyDetailOutputBoundary {

    /**
     * Call this method after successfully attain the company data
     * @param companyOverview central company data
     * @param financials financial data
     * @param news news related
     */
    void presentCompanyDetail(Company companyOverview, FinancialStatement financials, List<NewsArticle> news);

    /**
     * when data fail to attain call this method to get error message
     * @param message errormessage
     */
    void presentError(String message);
}