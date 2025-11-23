package app;

import javax.swing.SwingUtilities;

import api.AlphaVantagePriceGateway;
import api.Api;
import framework_and_driver.CompanyDetailPage;
import interface_adapter.CompanyDetailController;
import interface_adapter.CompanyDetailPresenter;
import interface_adapter.IntervalController;
import use_case.CompanyDetailInputBoundary;
import use_case.GetPriceByIntervalInteractor;
import use_case.PriceChartOutputBoundary;
import use_case.PriceDataAccessInterface;
import use_case.ViewCompanyDetailInteractor;

public class CompanyMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize API
            Api api = new Api("demo"); // TODO: Replace with actual API key
            
            // Create gateways
            // Use api.AlphaVantageCompanyGateway which implements use_case.CompanyGateway
            use_case.CompanyGateway companyGateway = new api.AlphaVantageCompanyGateway();
            
            // Create adapters for gateways that implement different interfaces
            data_access.AlphaVantageFinancialStatementGateway financialStatementGateway = 
                    new data_access.AlphaVantageFinancialStatementGateway(api);
            use_case.FinancialGateway financialGateway = 
                    new data_access.FinancialGatewayAdapter(financialStatementGateway);
            
            data_access.AlphaVantageNewsGateway oldNewsGateway = 
                    new data_access.AlphaVantageNewsGateway(api);
            use_case.NewsGateway newsGateway = 
                    new data_access.NewsGatewayAdapter(oldNewsGateway);
            
            PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();
            
            // Create UI
            CompanyDetailPage companyDetailPage = new CompanyDetailPage();
            
            // Create presenter (implements both CompanyDetailOutputBoundary and PriceChartOutputBoundary)
            CompanyDetailPresenter presenter = new CompanyDetailPresenter(companyDetailPage);
            
            // Create interactors
            CompanyDetailInputBoundary companyInteractor = new ViewCompanyDetailInteractor(
                    companyGateway, financialGateway, newsGateway, presenter);
            
            PriceChartOutputBoundary pricePresenter = presenter; // Same presenter handles both
            GetPriceByIntervalInteractor priceInteractor = new GetPriceByIntervalInteractor(
                    priceGateway, pricePresenter);
            
            // Create controllers
            CompanyDetailController companyController = new CompanyDetailController(companyInteractor);
            IntervalController chartController = new IntervalController(priceInteractor);
            
            // Wire up controllers
            companyDetailPage.setChartController(chartController);
            
            // Set visible
            companyDetailPage.setVisible(true);
            
            // Load default company (e.g., AAPL) for demo
            companyController.handleCompanyDetailRequest("AAPL");
            
            // Load initial chart data
            chartController.handleTimeChange("1D");
        });
    }
}
