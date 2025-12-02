
package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import api.Api;
import dataaccess.AlphaVantageCompanyGateway;
import dataaccess.AlphaVantageCompanyListDataAccess;
import dataaccess.AlphaVantageEconomicIndicatorGateway;
import dataaccess.AlphaVantageMarketIndexGateway;
import dataaccess.AlphaVantageSearchDataAccess;
import dataaccess.CompanyNameMapper;
import dataaccess.Top100Companies;
import entity.Company;
import entity.EconomicIndicator;
import entity.MarketIndex;
import frameworkanddriver.CompanyListPage;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.company_list.DataFormatters;
import interfaceadapter.controller.CompanyListController;
import interfaceadapter.controller.SearchCompanyController;
import interfaceadapter.presenter.CompanyListPresenter;
import interfaceadapter.presenter.SearchCompanyPresenter;
import interfaceadapter.view_model.CompanyListViewModel;
import interfaceadapter.view_model.SearchCompanyViewModel;
import usecase.company.CompanyGateway;
import usecase.company_list.CompanyListInteractor;
import usecase.search_company.SearchCompanyInteractor;

/**
 * Main entry point for the Company List application.
 * Follows Single Responsibility Principle - handles application startup and dependency wiring.
 */
public class CompanyListMain {

    /**
     * Entry point for the application. Initializes the GUI on the Swing
     * event-dispatch thread and displays the main window. If an unexpected
     * exception occurs during startup, an error dialog is shown.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            }
            catch (Exception ex) {
                System.err.println("Error starting application: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void createAndShowGUI() {
        // Setup UI components
        final CompanyListPage page = new CompanyListPage();
        final CompanyListViewModel companyListViewModel = new CompanyListViewModel();
        final SearchCompanyViewModel searchViewModel = new SearchCompanyViewModel();

        companyListViewModel.addPropertyChangeListener(page);
        searchViewModel.addPropertyChangeListener(page);

        // Setup API
        final String apiKey = getApiKey();
        final Api api = new Api(apiKey);
        final CompanyGateway companyGateway = new AlphaVantageCompanyGateway(api);

        // Setup presenters
        final CompanyListPresenter companyListPresenter =
                new CompanyListPresenter(page, companyListViewModel);
        final SearchCompanyPresenter searchPresenter =
                new SearchCompanyPresenter(page, searchViewModel);

        // Initialize search functionality
        final AlphaVantageSearchDataAccess searchDataAccess =
                new AlphaVantageSearchDataAccess(new ArrayList<>());
        final SearchCompanyInteractor searchInteractor =
                new SearchCompanyInteractor(searchDataAccess, searchPresenter);
        final SearchCompanyController searchController =
                new SearchCompanyController(searchInteractor);
        page.setSearchController(searchController);

        System.out.println("Search controller initialized");

        // Create and show window
        final JFrame frame = createFrame(page);
        frame.setVisible(true);
        System.out.println("Window opened!");

        // Display initial table with all 100 company names
        displayInitialTable(page);

        // Start progressive data loading in background
        startDataLoading(page, api, companyGateway, companyListPresenter, searchDataAccess);
    }

    /**
     * Retrieves the Alpha Vantage API key from the system environment.
     * If no key is found, a warning is printed and the default demo key is used.
     *
     * @return the configured API key, or "demo" if none is set
     */
    private static String getApiKey() {
        final String apiKey = dataaccess.EnvConfig.getAlphaVantageApiKey();
        if (apiKey == null || apiKey.isEmpty() || "demo".equals(apiKey)) {
            System.err.println("WARNING: ALPHA_VANTAGE_API_KEY not configured; using demo key from EnvConfig.");
            System.err.println("Set it in a .env file or as an environment variable: "
                    + "ALPHA_VANTAGE_API_KEY=your_key_here");
        }
        return apiKey;
    }

    /**
     * Creates and configures the main application frame.
     *
     * @param page the main UI page to display in the frame
     * @return a configured {@link JFrame} instance
     */
    private static JFrame createFrame(CompanyListPage page) {
        final JFrame frame = new JFrame("Billionaire - Stock Market Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(page);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    /**
     * Populates the initial table view with all 100 company tickers and names.
     * Only the top three companies begin loading full data immediately.
     *
     * @param page the UI page whose table will be updated
     */
    private static void displayInitialTable(CompanyListPage page) {
        final List<String> allTickers = Top100Companies.getAll();
        final List<CompanyDisplayData> initialDisplay = new ArrayList<>();

        for (int i = 0; i < allTickers.size(); i++) {
            final String ticker = allTickers.get(i);
            final String companyName = CompanyNameMapper.getCompanyName(ticker);

            if (i < 3) {
                // Top 3 will load full data
                initialDisplay.add(new CompanyDisplayData(
                        ticker, companyName, "—", "Loading...", "Loading..."
                ));
            }
            else {
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
     * Initiates progressive data loading in the background.
     *
     * <p>
     * This method creates a {@link CompanyDataLoader} to load company data
     * asynchronously and immediately starts its execution. A status message
     * is printed to indicate that the loading process has begun.
     *
     * @param page               the UI page that will display the company list
     * @param api                the API client used to retrieve data
     * @param companyGateway     the gateway providing access to stored company information
     * @param companyListPresenter the presenter responsible for updating the UI with results
     * @param searchDataAccess   the data access object for AlphaVantage symbol search results
     */
    private static void startDataLoading(
            CompanyListPage page,
            Api api,
            CompanyGateway companyGateway,
            CompanyListPresenter companyListPresenter,
            AlphaVantageSearchDataAccess searchDataAccess) {

        final CompanyDataLoader loader = new CompanyDataLoader(
                page, api, companyGateway, companyListPresenter, searchDataAccess
        );
        loader.execute();

        System.out.println("Progressive data loading started...");
    }

    /**
     * Handles progressive loading of company data in the background.
     * Loads market indices, economic indicators, and company details asynchronously.
     *
     * <p></p>
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
        protected Void doInBackground() {
            try {
                loadMarketIndices();
                loadEconomicIndicators();
                loadTop3Companies();
                setupCompanyListController();

                System.out.println("All data loaded! (" + loadedCompanies.size() + " companies with full data)");

            }
            catch (Exception ex) {
                System.err.println("Error during data loading: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
            }
            return null;
        }

        private void loadMarketIndices() {
            new Thread(() -> {
                try {
                    System.out.println("📈 Loading market indices...");
                    final AlphaVantageMarketIndexGateway marketIndexGateway =
                            new AlphaVantageMarketIndexGateway(api);
                    final List<MarketIndex> indices = marketIndexGateway.getMarketIndices();

                    SwingUtilities.invokeLater(() -> {
                        page.setMarketIndices(indices);
                        System.out.println("Market indices loaded!");
                    });
                }
                catch (Exception ex) {
                    System.err.println("Market indices error: " + ex.getMessage());
                }
            }).start();
        }

        private void loadEconomicIndicators() {
            new Thread(() -> {
                try {
                    System.out.println("Loading economic indicators...");
                    final AlphaVantageEconomicIndicatorGateway economicGateway =
                            new AlphaVantageEconomicIndicatorGateway(api);
                    final List<EconomicIndicator> indicators = economicGateway.getEconomicIndicators();

                    SwingUtilities.invokeLater(() -> {
                        page.setEconomicIndicators(indicators);
                        System.out.println("Economic indicators loaded!");
                    });
                }
                catch (Exception ex) {
                    System.err.println("Economic indicators error: " + ex.getMessage());
                }
            }).start();
        }

        private void loadTop3Companies() {
            System.out.println("Loading detailed data for top 3 companies...");
            final List<String> top3Tickers = allTickers.subList(0, Math.min(3, allTickers.size()));

            int count = 0;
            for (String ticker : top3Tickers) {
                try {
                    count++;
                    System.out.printf("  Loading %d/3: %s%n", count, ticker);

                    final Company company = companyGateway.fetchOverview(ticker);

                    if (company != null && company.getName() != null && !company.getName().isEmpty()) {
                        loadedCompanies.put(ticker, company);
                        publish(new CompanyUpdate(ticker));
                        System.out.println("  ✅ " + company.getName());

                        SwingUtilities.invokeLater(() -> searchDataAccess.updateCache(new ArrayList<>(loadedCompanies.values())));
                    }
                    else {
                        System.err.println("  ⚠️ No data for " + ticker);
                    }

                    if (count < top3Tickers.size()) {
                        Thread.sleep(12000);
                    }

                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.err.println("Interrupted");
                    break;
                }
                catch (Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            }
        }

        private void setupCompanyListController() {
            if (!loadedCompanies.isEmpty()) {
                final AlphaVantageCompanyListDataAccess companyListDataAccess =
                        new AlphaVantageCompanyListDataAccess(companyGateway, true) {
                            @Override
                            public List<Company> getCompanyList() {
                                return new ArrayList<>(loadedCompanies.values());
                            }
                        };

                final CompanyListInteractor companyListInteractor =
                        new CompanyListInteractor(companyListDataAccess, companyListPresenter);

                final CompanyListController companyListController =
                        new CompanyListController(companyListInteractor);

                SwingUtilities.invokeLater(() -> page.setListController(companyListController));
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
            final List<CompanyDisplayData> currentDisplay = new ArrayList<>();

            for (int i = 0; i < allTickers.size(); i++) {
                final String ticker = allTickers.get(i);

                if (loadedCompanies.containsKey(ticker)) {
                    final Company company = loadedCompanies.get(ticker);
                    currentDisplay.add(new CompanyDisplayData(
                            company.getSymbol(),
                            company.getName(),
                            company.getCountry(),
                            DataFormatters.formatMarketCap(company.getMarketCapitalization()),
                            DataFormatters.formatPeRatio(company.getPeRatio())
                    ));
                }
                else if (i < 3) {
                    currentDisplay.add(new CompanyDisplayData(
                            ticker,
                            CompanyNameMapper.getCompanyName(ticker),
                            "—",
                            "Loading...",
                            "Loading..."
                    ));
                }
                else {
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

            CompanyUpdate(String ticker) {
            }
        }
    }
}
