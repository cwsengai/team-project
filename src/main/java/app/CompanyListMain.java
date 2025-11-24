package app;

import api.Api;
import data_access.AlphaVantageCompanyGateway;
import data_access.AlphaVantageCompanyListDataAccess;
import data_access.AlphaVantageSearchDataAccess;
import data_access.AlphaVantageMarketIndexGateway;
import data_access.AlphaVantageEconomicIndicatorGateway;
import data_access.Top100Companies;
import entity.Company;
import entity.EconomicIndicator;
import entity.MarketIndex;
import framework_and_driver.CompanyListPage;
import interface_adapter.company_list.CompanyDisplayData;
import interface_adapter.controller.CompanyListController;
import interface_adapter.controller.SearchCompanyController;
import interface_adapter.presenter.CompanyListPresenter;
import interface_adapter.presenter.SearchCompanyPresenter;
import interface_adapter.view_model.CompanyListViewModel;
import interface_adapter.view_model.SearchCompanyViewModel;
import use_case.company.CompanyGateway;
import use_case.EconomicIndicatorGateway;
import use_case.MarketIndexGateway;
import use_case.company_list.CompanyListInteractor;
import use_case.search_company.SearchCompanyInteractor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // ========== 1. FRAMEWORK & DRIVERS LAYER ==========
        CompanyListPage page = new CompanyListPage();

        CompanyListViewModel companyListViewModel = new CompanyListViewModel();
        SearchCompanyViewModel searchViewModel = new SearchCompanyViewModel();

        companyListViewModel.addPropertyChangeListener(page);
        searchViewModel.addPropertyChangeListener(page);

        // ========== 2. DATA ACCESS LAYER ==========
        String apiKey = System.getenv("ALPHA_VANTAGE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("WARNING: ALPHA_VANTAGE_API_KEY environment variable not set!");
            System.err.println("Set it with: export ALPHA_VANTAGE_API_KEY=your_key_here");
            apiKey = "demo";
        }

        Api api = new Api(apiKey);
        CompanyGateway companyGateway = new AlphaVantageCompanyGateway(api);

        // ========== 3. INTERFACE ADAPTERS LAYER ==========
        CompanyListPresenter companyListPresenter =
                new CompanyListPresenter(page, companyListViewModel);
        SearchCompanyPresenter searchPresenter =
                new SearchCompanyPresenter(page, searchViewModel);

        // ========== 4. CREATE AND SHOW WINDOW ==========
        JFrame frame = new JFrame("Billionaire - Stock Market Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(page);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println("✅ Window opened!");

        // ========== 5. SHOW ALL 100 COMPANIES IMMEDIATELY (Names Only) ==========
        List<String> allTickers = Top100Companies.getAll();
        List<CompanyDisplayData> initialDisplay = new ArrayList<>();

// Create display data for all 100 companies (names only, no API calls)
        for (int i = 0; i < allTickers.size(); i++) {
            String ticker = allTickers.get(i);

            if (i < 3) {
                initialDisplay.add(new CompanyDisplayData(
                        ticker,
                        getCompanyNameGuess(ticker),
                        "—",
                        "Loading...",
                        "Loading..."
                ));
            } else {
                initialDisplay.add(new CompanyDisplayData(
                        ticker,
                        getCompanyNameGuess(ticker),
                        "—",
                        "—",
                        "—"
                ));
            }
        }

// Show all 100 immediately!
        page.updateTable(initialDisplay);
        System.out.println("✅ Showing all 100 companies (names only)");
        System.out.println("📊 Loading full data for top 10...");

        // ========== 6. LOAD FULL DATA FOR TOP 10 IN BACKGROUND ==========
        new SwingWorker<Void, Object>() {
            private final Map<String, Company> loadedCompanies = new HashMap<>();

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // ===== STEP 1: Load Market Indices (in parallel) =====
                    new Thread(() -> {
                        try {
                            System.out.println("📈 Loading market indices...");
                            MarketIndexGateway marketIndexGateway = new AlphaVantageMarketIndexGateway(api);
                            List<MarketIndex> indices = marketIndexGateway.getMarketIndices();
                            SwingUtilities.invokeLater(() -> {
                                page.setMarketIndices(indices);
                                System.out.println("✅ Market indices loaded!");
                            });
                        } catch (Exception e) {
                            System.err.println("❌ Market indices error: " + e.getMessage());
                        }
                    }).start();

                    // ===== STEP 2: Load Economic Indicators (in parallel) =====
                    new Thread(() -> {
                        try {
                            System.out.println("📊 Loading economic indicators...");
                            EconomicIndicatorGateway economicGateway = new AlphaVantageEconomicIndicatorGateway(api);
                            List<EconomicIndicator> indicators = economicGateway.getEconomicIndicators();
                            SwingUtilities.invokeLater(() -> {
                                page.setEconomicIndicators(indicators);
                                System.out.println("✅ Economic indicators loaded!");
                            });
                        } catch (Exception e) {
                            System.err.println("❌ Economic indicators error: " + e.getMessage());
                        }
                    }).start();

                    // ===== STEP 3: Load FULL DATA for first 3 companies only =====
                    System.out.println("🏢 Loading detailed data for top 3 companies...");
                    List<String> top3Tickers = allTickers.subList(0, Math.min(3, allTickers.size()));  // ✅ Changed to 3

                    int count = 0;
                    for (String ticker : top3Tickers) {
                        try {
                            count++;
                            System.out.println(String.format("  Loading %d/3: %s", count, ticker));  // ✅ Changed to 3

                            Company company = companyGateway.fetchOverview(ticker);

                            if (company != null && company.getName() != null && !company.getName().isEmpty()) {
                                loadedCompanies.put(ticker, company);
                                publish(new CompanyUpdate(ticker, company));
                                System.out.println("  ✅ " + company.getName());
                            } else {
                                System.err.println("  ⚠️ No data for " + ticker);
                            }

                            // Rate limit delay
                            if (count < top3Tickers.size()) {
                                Thread.sleep(12000);
                            }

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("  ❌ Interrupted");
                            break;
                        } catch (Exception e) {
                            System.err.println("  ❌ Error: " + e.getMessage());
                        }
                    }

                    // ===== STEP 4: Setup Search with Loaded Companies =====
                    if (!loadedCompanies.isEmpty()) {
                        AlphaVantageSearchDataAccess searchDataAccess =
                                new AlphaVantageSearchDataAccess(new ArrayList<>(loadedCompanies.values()));

                        AlphaVantageCompanyListDataAccess companyListDataAccess =
                                new AlphaVantageCompanyListDataAccess(companyGateway, true) {
                                    @Override
                                    public List<Company> getCompanyList() {
                                        return new ArrayList<>(loadedCompanies.values());
                                    }
                                };

                        CompanyListInteractor companyListInteractor =
                                new CompanyListInteractor(companyListDataAccess, companyListPresenter);
                        SearchCompanyInteractor searchInteractor =
                                new SearchCompanyInteractor(searchDataAccess, searchPresenter);

                        CompanyListController companyListController =
                                new CompanyListController(companyListInteractor);
                        SearchCompanyController searchController =
                                new SearchCompanyController(searchInteractor);

                        SwingUtilities.invokeLater(() -> {
                            page.setListController(companyListController);
                            page.setSearchController(searchController);
                        });
                    }

                    System.out.println("🎉 All data loaded! (" + loadedCompanies.size() + " companies with full data)");

                } catch (Exception e) {
                    System.err.println("❌ Error during data loading: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(List<Object> chunks) {
                for (Object chunk : chunks) {
                    if (chunk instanceof CompanyUpdate) {
                        CompanyUpdate update = (CompanyUpdate) chunk;

                        // Update the display data with full information
                        List<CompanyDisplayData> currentDisplay = new ArrayList<>();

                        for (int i = 0; i < allTickers.size(); i++) {
                            String ticker = allTickers.get(i);

                            if (loadedCompanies.containsKey(ticker)) {
                                // ✅ This company has full data loaded
                                Company company = loadedCompanies.get(ticker);
                                currentDisplay.add(new CompanyDisplayData(
                                        company.getSymbol(),
                                        company.getName(),
                                        company.getCountry(),
                                        formatMarketCap(company.getMarketCapitalization()),
                                        formatPeRatio(company.getPeRatio())
                                ));
                            } else if (i < 3) {
                                // ✅ Top 10 but not loaded yet - show loading
                                currentDisplay.add(new CompanyDisplayData(
                                        ticker,
                                        getCompanyNameGuess(ticker),
                                        "—",
                                        "Loading...",
                                        "Loading..."
                                ));
                            } else {
                                // ✅ Companies 11-100 - show placeholder
                                currentDisplay.add(new CompanyDisplayData(
                                        ticker,
                                        getCompanyNameGuess(ticker),
                                        "—",
                                        "—",
                                        "—"
                                ));
                            }
                        }

                        page.updateTable(currentDisplay);
                    }
                }
            }

            @Override
            protected void done() {
                System.out.println("✅ All data loading complete!");
            }

            private String formatMarketCap(double marketCap) {
                if (marketCap >= 1_000_000_000_000.0) {
                    return String.format("$%.1fT", marketCap / 1_000_000_000_000.0);
                } else if (marketCap >= 1_000_000_000.0) {
                    return String.format("$%.1fB", marketCap / 1_000_000_000.0);
                } else if (marketCap >= 1_000_000.0) {
                    return String.format("$%.1fM", marketCap / 1_000_000.0);
                } else {
                    return String.format("$%.0f", marketCap);
                }
            }

            private String formatPeRatio(double peRatio) {
                if (peRatio <= 0) return "N/A";
                return String.format("%.2f", peRatio);
            }
        }.execute();

        System.out.println("Ready! Full data will load progressively for top 10...");
    }

    /**
     * Guess company name from ticker symbol.
     * This is just for display - real names will load for top 10.
     */
    private static String getCompanyNameGuess(String ticker) {
        Map<String, String> knownNames = new HashMap<>();
        knownNames.put("AAPL", "Apple Inc");
        knownNames.put("MSFT", "Microsoft Corporation");
        knownNames.put("GOOGL", "Alphabet Inc");

        return knownNames.getOrDefault(ticker, ticker);
    }

    // Helper class for passing company updates
    private static class CompanyUpdate {
        final String ticker;
        final Company company;
        CompanyUpdate(String ticker, Company company) {
            this.ticker = ticker;
            this.company = company;
        }
    }
}