package app;

import api.Api;
import dataaccess.*;
import entity.Company;
import entity.EconomicIndicator;
import entity.MarketIndex;
import frameworkanddriver.CompanyListPage;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.controller.CompanyListController;
import interfaceadapter.controller.SearchCompanyController;
import interfaceadapter.presenter.CompanyListPresenter;
import interfaceadapter.presenter.SearchCompanyPresenter;
import interfaceadapter.company_list.DataFormatters;
import interfaceadapter.view_model.CompanyListViewModel;
import interfaceadapter.view_model.SearchCompanyViewModel;
import usecase.company.CompanyGateway;
import usecase.company_list.CompanyListInteractor;
import usecase.search_company.SearchCompanyInteractor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for the Company List application.
 * Follows Single Responsibility Principle - handles application startup and dependency wiring.
 */
public class CompanyListMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void createAndShowGUI() {
        // Setup UI components
        CompanyListPage page = new CompanyListPage();
        CompanyListViewModel companyListViewModel = new CompanyListViewModel();
        SearchCompanyViewModel searchViewModel = new SearchCompanyViewModel();

        companyListViewModel.addPropertyChangeListener(page);
        searchViewModel.addPropertyChangeListener(page);

        // Setup API
        String apiKey = getApiKey();
        Api api = new Api(apiKey);
        CompanyGateway companyGateway = new AlphaVantageCompanyGateway(api);

        // Setup presenters
        CompanyListPresenter companyListPresenter =
                new CompanyListPresenter(page, companyListViewModel);
        SearchCompanyPresenter searchPresenter =
                new SearchCompanyPresenter(page, searchViewModel);

        // Initialize search functionality
        AlphaVantageSearchDataAccess searchDataAccess =
                new AlphaVantageSearchDataAccess(new ArrayList<>());
        SearchCompanyInteractor searchInteractor =
                new SearchCompanyInteractor(searchDataAccess, searchPresenter);
        SearchCompanyController searchController =
                new SearchCompanyController(searchInteractor);
        page.setSearchController(searchController);

        System.out.println("Search controller initialized");

        // Create and show window
        JFrame frame = createFrame(page);
        frame.setVisible(true);
        System.out.println("Window opened!");

        // Display initial table with all 100 company names
        displayInitialTable(page);

        // Start progressive data loading in background
        startDataLoading(page, api, companyGateway, companyListPresenter, searchDataAccess);
    }

    /**
     * Get API key from environment or use demo key.
     */
    private static String getApiKey() {
        String apiKey = System.getenv("ALPHA_VANTAGE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("WARNING: ALPHA_VANTAGE_API_KEY environment variable not set!");
            System.err.println("Set it with: export ALPHA_VANTAGE_API_KEY=your_key_here");
            return "demo";
        }
        return apiKey;
    }

    /**
     * Create the main application frame.
     */
    private static JFrame createFrame(CompanyListPage page) {
        JFrame frame = new JFrame("Billionaire - Stock Market Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(page);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    /**
     * Display initial table with all 100 company names.
     */
    private static void displayInitialTable(CompanyListPage page) {
        List<String> allTickers = Top100Companies.getAll();
        List<CompanyDisplayData> initialDisplay = new ArrayList<>();

        for (int i = 0; i < allTickers.size(); i++) {
            String ticker = allTickers.get(i);
            String companyName = CompanyNameMapper.getCompanyName(ticker);

            if (i < 3) {
                // Top 3 will load full data
                initialDisplay.add(new CompanyDisplayData(
                        ticker, companyName, "—", "Loading...", "Loading..."
                ));
            } else {
                // Rest show name only
                initialDisplay.add(new CompanyDisplayData(
                        ticker, companyName, "—", "—", "—"
                ));
            }
        }

        page.updateTable(initialDisplay);
        System.out.println("Showing all 100 companies (names only)");
        System.out.println("Loading full data for top 3...");
    }

    /**
     * Start progressive data loading in background.
     */
    private static void startDataLoading(
            CompanyListPage page,
            Api api,
            CompanyGateway companyGateway,
            CompanyListPresenter companyListPresenter,
            AlphaVantageSearchDataAccess searchDataAccess) {

        CompanyDataLoader loader = new CompanyDataLoader(
                page, api, companyGateway, companyListPresenter, searchDataAccess
        );
        loader.execute();

        System.out.println("Progressive data loading started...");
    }

    /**
     * Handles progressive loading of company data in the background.
     * Loads market indices, economic indicators, and company details asynchronously.
     *
     * This is an inner class because it's only used by CompanyListMain during startup.
     */
    private static class CompanyDataLoader extends SwingWorker<Void, Object> {

        private final CompanyListPage page;
        private final Api api;
        private final CompanyGateway companyGateway;
        private final CompanyListPresenter companyListPresenter;
        private final AlphaVantageSearchDataAccess searchDataAccess;
        private final List<String> allTickers;
        private final Map<String, Company> loadedCompanies = new HashMap<>();

        public CompanyDataLoader(
                CompanyListPage page,
                Api api,
                CompanyGateway companyGateway,
                CompanyListPresenter companyListPresenter,
                AlphaVantageSearchDataAccess searchDataAccess) {
            this.page = page;
            this.api = api;
            this.companyGateway = companyGateway;
            this.companyListPresenter = companyListPresenter;
            this.searchDataAccess = searchDataAccess;
            this.allTickers = Top100Companies.getAll();
        }

        @Override
        protected Void doInBackground() throws Exception {
            try {
                loadMarketIndices();
                loadEconomicIndicators();
                loadTop3Companies();
                setupCompanyListController();

                System.out.println("All data loaded! (" + loadedCompanies.size() + " companies with full data)");

            } catch (Exception e) {
                System.err.println("Error during data loading: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private void loadMarketIndices() {
            new Thread(() -> {
                try {
                    System.out.println("📈 Loading market indices...");
                    AlphaVantageMarketIndexGateway marketIndexGateway =
                            new AlphaVantageMarketIndexGateway(api);
                    List<MarketIndex> indices = marketIndexGateway.getMarketIndices();

                    SwingUtilities.invokeLater(() -> {
                        page.setMarketIndices(indices);
                        System.out.println("Market indices loaded!");
                    });
                } catch (Exception e) {
                    System.err.println("Market indices error: " + e.getMessage());
                }
            }).start();
        }

        private void loadEconomicIndicators() {
            new Thread(() -> {
                try {
                    System.out.println("Loading economic indicators...");
                    AlphaVantageEconomicIndicatorGateway economicGateway =
                            new AlphaVantageEconomicIndicatorGateway(api);
                    List<EconomicIndicator> indicators = economicGateway.getEconomicIndicators();

                    SwingUtilities.invokeLater(() -> {
                        page.setEconomicIndicators(indicators);
                        System.out.println("Economic indicators loaded!");
                    });
                } catch (Exception e) {
                    System.err.println("Economic indicators error: " + e.getMessage());
                }
            }).start();
        }

        private void loadTop3Companies() throws Exception {
            System.out.println("Loading detailed data for top 3 companies...");
            List<String> top3Tickers = allTickers.subList(0, Math.min(3, allTickers.size()));

            int count = 0;
            for (String ticker : top3Tickers) {
                try {
                    count++;
                    System.out.println(String.format("  Loading %d/3: %s", count, ticker));

                    Company company = companyGateway.fetchOverview(ticker);

                    if (company != null && company.getName() != null && !company.getName().isEmpty()) {
                        loadedCompanies.put(ticker, company);
                        publish(new CompanyUpdate(ticker, company));
                        System.out.println("  ✅ " + company.getName());

                        SwingUtilities.invokeLater(() -> {
                            searchDataAccess.updateCache(new ArrayList<>(loadedCompanies.values()));
                        });
                    } else {
                        System.err.println("  ⚠️ No data for " + ticker);
                    }

                    if (count < top3Tickers.size()) {
                        Thread.sleep(12000);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Interrupted");
                    break;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        private void setupCompanyListController() {
            if (!loadedCompanies.isEmpty()) {
                AlphaVantageCompanyListDataAccess companyListDataAccess =
                        new AlphaVantageCompanyListDataAccess(companyGateway, true) {
                            @Override
                            public List<Company> getCompanyList() {
                                return new ArrayList<>(loadedCompanies.values());
                            }
                        };

                CompanyListInteractor companyListInteractor =
                        new CompanyListInteractor(companyListDataAccess, companyListPresenter);

                CompanyListController companyListController =
                        new CompanyListController(companyListInteractor);

                SwingUtilities.invokeLater(() -> {
                    page.setListController(companyListController);
                });
            }
        }

        @Override
        protected void process(List<Object> chunks) {
            for (Object chunk : chunks) {
                if (chunk instanceof CompanyUpdate) {
                    updateTableWithLoadedData();
                }
            }
        }

        private void updateTableWithLoadedData() {
            List<CompanyDisplayData> currentDisplay = new ArrayList<>();

            for (int i = 0; i < allTickers.size(); i++) {
                String ticker = allTickers.get(i);

                if (loadedCompanies.containsKey(ticker)) {
                    Company company = loadedCompanies.get(ticker);
                    currentDisplay.add(new CompanyDisplayData(
                            company.getSymbol(),
                            company.getName(),
                            company.getCountry(),
                            DataFormatters.formatMarketCap(company.getMarketCapitalization()),
                            DataFormatters.formatPeRatio(company.getPeRatio())
                    ));
                } else if (i < 3) {
                    currentDisplay.add(new CompanyDisplayData(
                            ticker,
                            CompanyNameMapper.getCompanyName(ticker),
                            "—",
                            "Loading...",
                            "Loading..."
                    ));
                } else {
                    currentDisplay.add(new CompanyDisplayData(
                            ticker,
                            CompanyNameMapper.getCompanyName(ticker),
                            "—",
                            "—",
                            "—"
                    ));
                }
            }

            page.updateTable(currentDisplay);
        }

        @Override
        protected void done() {
            System.out.println("All background loading complete!");
        }

        /**
         * Helper class for passing company updates.
         */
        private static class CompanyUpdate {
            final String ticker;
            final Company company;

            CompanyUpdate(String ticker, Company company) {
                this.ticker = ticker;
                this.company = company;
            }
        }
    }
}