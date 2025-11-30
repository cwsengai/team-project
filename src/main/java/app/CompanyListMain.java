package app;

import api.Api;
import dataaccess.AlphaVantageCompanyGateway;
import dataaccess.AlphaVantageCompanyListDataAccess;
import dataaccess.AlphaVantageSearchDataAccess;
import dataaccess.AlphaVantageMarketIndexGateway;
import dataaccess.AlphaVantageEconomicIndicatorGateway;
import dataaccess.Top100Companies;
import entity.Company;
import entity.EconomicIndicator;
import entity.MarketIndex;
import frameworkanddriver.CompanyListPage;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.controller.CompanyListController;
import interfaceadapter.controller.SearchCompanyController;
import interfaceadapter.presenter.CompanyListPresenter;
import interfaceadapter.presenter.SearchCompanyPresenter;
import interfaceadapter.view_model.CompanyListViewModel;
import interfaceadapter.view_model.SearchCompanyViewModel;
import usecase.company.CompanyGateway;
import usecase.EconomicIndicatorGateway;
import usecase.MarketIndexGateway;
import usecase.company_list.CompanyListInteractor;
import usecase.search_company.SearchCompanyInteractor;

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

        // ✅ INITIALIZE SEARCH IMMEDIATELY (with empty list for now)
        AlphaVantageSearchDataAccess searchDataAccess =
                new AlphaVantageSearchDataAccess(new ArrayList<>());
        SearchCompanyInteractor searchInteractor =
                new SearchCompanyInteractor(searchDataAccess, searchPresenter);
        SearchCompanyController searchController =
                new SearchCompanyController(searchInteractor);
        page.setSearchController(searchController);  // ✅ Set it right away!
        System.out.println("✅ Search controller initialized (will update with data as it loads)");

        // ========== 4. CREATE AND SHOW WINDOW ==========
        JFrame frame = new JFrame("Billionaire - Stock Market Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(page);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println("✅ Window opened!");
        System.out.println("📊 Starting progressive data loading...");

        // ========== 5. SHOW ALL 100 COMPANIES IMMEDIATELY (Names Only) ==========
        List<String> allTickers = Top100Companies.getAll();
        List<CompanyDisplayData> initialDisplay = new ArrayList<>();

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

        page.updateTable(initialDisplay);
        System.out.println("✅ Showing all 100 companies (names only)");
        System.out.println("📊 Loading full data for top 3...");

        // ========== 6. PROGRESSIVE LOADING IN BACKGROUND ==========
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

                    // ===== STEP 3: Load FULL DATA for first 3 companies =====
                    System.out.println("🏢 Loading detailed data for top 3 companies...");
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

                                // ✅ UPDATE SEARCH DATA AS COMPANIES LOAD
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
                            System.err.println("  ❌ Interrupted");
                            break;
                        } catch (Exception e) {
                            System.err.println("  ❌ Error: " + e.getMessage());
                        }
                    }

                    // ===== STEP 4: Setup Company List Controller =====
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

                        List<CompanyDisplayData> currentDisplay = new ArrayList<>();

                        for (int i = 0; i < allTickers.size(); i++) {
                            String ticker = allTickers.get(i);

                            if (loadedCompanies.containsKey(ticker)) {
                                Company company = loadedCompanies.get(ticker);
                                currentDisplay.add(new CompanyDisplayData(
                                        company.getSymbol(),
                                        company.getName(),
                                        company.getCountry(),
                                        formatMarketCap(company.getMarketCapitalization()),
                                        formatPeRatio(company.getPeRatio())
                                ));
                            } else if (i < 3) {
                                currentDisplay.add(new CompanyDisplayData(
                                        ticker,
                                        getCompanyNameGuess(ticker),
                                        "—",
                                        "Loading...",
                                        "Loading..."
                                ));
                            } else {
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
                System.out.println("✅ All background loading complete!");
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

        System.out.println("Ready! Companies will appear as they load...");
    }

    /**
     * Guess company name from ticker symbol.
     * This is just for display - real names will load for top 10.
     */
    /**
     * Get company name from ticker symbol.
     * Used for display when full data isn't loaded.
     */
    private static String getCompanyNameGuess(String ticker) {
        Map<String, String> knownNames = new HashMap<>();

        // Top 20
        knownNames.put("AAPL", "Apple Inc");
        knownNames.put("MSFT", "Microsoft Corporation");
        knownNames.put("GOOGL", "Alphabet Inc");
        knownNames.put("AMZN", "Amazon.com Inc");
        knownNames.put("NVDA", "NVIDIA Corporation");
        knownNames.put("META", "Meta Platforms Inc");
        knownNames.put("TSLA", "Tesla Inc");
        knownNames.put("BRK.B", "Berkshire Hathaway");
        knownNames.put("V", "Visa Inc");
        knownNames.put("UNH", "UnitedHealth Group");
        knownNames.put("JNJ", "Johnson & Johnson");
        knownNames.put("WMT", "Walmart Inc");
        knownNames.put("JPM", "JPMorgan Chase");
        knownNames.put("MA", "Mastercard Inc");
        knownNames.put("XOM", "Exxon Mobil");
        knownNames.put("PG", "Procter & Gamble");
        knownNames.put("HD", "Home Depot");
        knownNames.put("CVX", "Chevron Corporation");
        knownNames.put("AVGO", "Broadcom Inc");
        knownNames.put("MRK", "Merck & Co");

        // 21-40
        knownNames.put("ABBV", "AbbVie Inc");
        knownNames.put("PEP", "PepsiCo Inc");
        knownNames.put("KO", "Coca-Cola Company");
        knownNames.put("COST", "Costco Wholesale");
        knownNames.put("ADBE", "Adobe Inc");
        knownNames.put("TMO", "Thermo Fisher Scientific");
        knownNames.put("MCD", "McDonald's Corporation");
        knownNames.put("CSCO", "Cisco Systems");
        knownNames.put("ACN", "Accenture");
        knownNames.put("ABT", "Abbott Laboratories");
        knownNames.put("NKE", "Nike Inc");
        knownNames.put("LLY", "Eli Lilly");
        knownNames.put("TXN", "Texas Instruments");
        knownNames.put("DHR", "Danaher Corporation");
        knownNames.put("CRM", "Salesforce Inc");
        knownNames.put("NEE", "NextEra Energy");
        knownNames.put("DIS", "Walt Disney Company");
        knownNames.put("VZ", "Verizon Communications");
        knownNames.put("CMCSA", "Comcast Corporation");
        knownNames.put("ORCL", "Oracle Corporation");

        // 41-60
        knownNames.put("INTC", "Intel Corporation");
        knownNames.put("NFLX", "Netflix Inc");
        knownNames.put("AMD", "Advanced Micro Devices");
        knownNames.put("PFE", "Pfizer Inc");
        knownNames.put("PM", "Philip Morris International");
        knownNames.put("T", "AT&T Inc");
        knownNames.put("UPS", "United Parcel Service");
        knownNames.put("BA", "Boeing Company");
        knownNames.put("IBM", "IBM");
        knownNames.put("QCOM", "Qualcomm Inc");
        knownNames.put("HON", "Honeywell International");
        knownNames.put("AMGN", "Amgen Inc");
        knownNames.put("RTX", "Raytheon Technologies");
        knownNames.put("UNP", "Union Pacific");
        knownNames.put("SPGI", "S&P Global");
        knownNames.put("LOW", "Lowe's Companies");
        knownNames.put("CAT", "Caterpillar Inc");
        knownNames.put("SBUX", "Starbucks Corporation");
        knownNames.put("GS", "Goldman Sachs");
        knownNames.put("INTU", "Intuit Inc");

        // 61-80
        knownNames.put("AXP", "American Express");
        knownNames.put("CVS", "CVS Health");
        knownNames.put("DE", "Deere & Company");
        knownNames.put("BLK", "BlackRock Inc");
        knownNames.put("MDLZ", "Mondelez International");
        knownNames.put("GILD", "Gilead Sciences");
        knownNames.put("ADP", "Automatic Data Processing");
        knownNames.put("MMM", "3M Company");
        knownNames.put("TJX", "TJX Companies");
        knownNames.put("BKNG", "Booking Holdings");
        knownNames.put("ISRG", "Intuitive Surgical");
        knownNames.put("AMT", "American Tower");
        knownNames.put("REGN", "Regeneron Pharmaceuticals");
        knownNames.put("CI", "Cigna Corporation");
        knownNames.put("VRTX", "Vertex Pharmaceuticals");
        knownNames.put("CB", "Chubb Limited");
        knownNames.put("MO", "Altria Group");
        knownNames.put("SYK", "Stryker Corporation");
        knownNames.put("ZTS", "Zoetis Inc");
        knownNames.put("BDX", "Becton Dickinson");

        // 81-100
        knownNames.put("TGT", "Target Corporation");
        knownNames.put("SO", "Southern Company");
        knownNames.put("USB", "U.S. Bancorp");
        knownNames.put("PLD", "Prologis Inc");
        knownNames.put("DUK", "Duke Energy");
        knownNames.put("CME", "CME Group");
        knownNames.put("CSX", "CSX Corporation");
        knownNames.put("CL", "Colgate-Palmolive");
        knownNames.put("ITW", "Illinois Tool Works");
        knownNames.put("NSC", "Norfolk Southern");
        knownNames.put("APD", "Air Products and Chemicals");
        knownNames.put("EOG", "EOG Resources");
        knownNames.put("WM", "Waste Management");
        knownNames.put("SHW", "Sherwin-Williams");
        knownNames.put("MCO", "Moody's Corporation");
        knownNames.put("CCI", "Crown Castle");
        knownNames.put("EL", "Estée Lauder");
        knownNames.put("SCHW", "Charles Schwab");
        knownNames.put("AON", "Aon plc");
        knownNames.put("HUM", "Humana Inc");

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