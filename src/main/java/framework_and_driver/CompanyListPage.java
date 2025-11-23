package framework_and_driver;

import data_access.AlphaVantageCompanyGateway;
import entity.PriceHistory;
import entity.PricePoint;
import entity.TimeInterval;
import interface_adapter.company_list.CompanyDisplayData;
import interface_adapter.controller.CompanyListController;
import interface_adapter.controller.SearchCompanyController;
import interface_adapter.presenter.CompanyListPresenter;
import interface_adapter.presenter.SearchCompanyPresenter;
import interface_adapter.view_model.CompanyListViewModel;
import interface_adapter.view_model.SearchCompanyViewModel;
import use_case.company_list.CompanyListInteractor;
import use_case.search_company.SearchCompanyInteractor;
import org.knowm.xchart.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main page showing market indices, economic indicators, and company list.
 * Implements User Stories 1 (Company List) and 2 (Search).
 */
public class CompanyListPage extends JPanel implements PropertyChangeListener {
    private final CompanyListController listController;
    private final SearchCompanyController searchController;
    private final CompanyListViewModel listViewModel;
    private final SearchCompanyViewModel searchViewModel;
    private final AlphaVantageCompanyGateway gateway;

    private JTable companyTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CompanyListPage() {
        // Setup dependencies - Clean Architecture wiring
        gateway = new AlphaVantageCompanyGateway();

        // Company List use case
        listViewModel = new CompanyListViewModel();
        CompanyListPresenter listPresenter = new CompanyListPresenter(listViewModel);
        CompanyListInteractor listInteractor = new CompanyListInteractor(gateway, listPresenter);
        listController = new CompanyListController(listInteractor);

        // Search use case
        searchViewModel = new SearchCompanyViewModel();
        SearchCompanyPresenter searchPresenter = new SearchCompanyPresenter(searchViewModel);
        SearchCompanyInteractor searchInteractor = new SearchCompanyInteractor(gateway, searchPresenter);
        searchController = new SearchCompanyController(searchInteractor);

        // Subscribe to view model changes
        listViewModel.addPropertyChangeListener(this);
        searchViewModel.addPropertyChangeListener(this);

        // Build UI
        setupUI();

        // Load initial data
        listController.loadCompanyList();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("🏦 BILLIONAIRE");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(title, BorderLayout.WEST);

        JButton loginButton = new JButton("Signup/ Login");
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        headerPanel.add(loginButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Economic Indicators (simplified - no charts for demo simplicity)
        JPanel economicPanel = createEconomicIndicatorsPanel();
        contentPanel.add(economicPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Company List Table
        JPanel companyPanel = createCompanyListPanel();
        contentPanel.add(companyPanel);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private JPanel createEconomicIndicatorsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 3, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Economic Indicators"));

        panel.add(new JLabel("<html><b>Indicator</b></html>"));
        panel.add(new JLabel("<html><b>Latest Value</b></html>"));
        panel.add(new JLabel("<html><b>Last Updated</b></html>"));

        addIndicatorRow(panel, "10-Year Breakeven Inflation Rate", "2.3 %", "2025-11-06");
        addIndicatorRow(panel, "Federal Funds Effective Rate", "4.4 %", "2025-11-06");
        addIndicatorRow(panel, "10-Year Treasury Constant Maturity Yield", "4.0 %", "2025-11-06");
        addIndicatorRow(panel, "Sticky Price Consumer Price Index", "3.1 %", "2025-10-24");
        addIndicatorRow(panel, "U.S. Unemployment Rate", "3.6 %", "2025-10-01");

        return panel;
    }

    private void addIndicatorRow(JPanel panel, String name, String value, String date) {
        panel.add(new JLabel(name));
        panel.add(new JLabel(value));
        panel.add(new JLabel(date));
    }

    private JPanel createCompanyListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Title and search bar at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Top 100 Fortune Companies");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        // Search panel on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.addActionListener(e -> performSearch());

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearButton.addActionListener(e -> clearSearch());

        // Allow Enter key to search
        searchField.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Create the table
        String[] columns = {"Symbol", "Company", "Country", "Market Cap", "P/E Ratio"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        companyTable = new JTable(tableModel);
        companyTable.setRowHeight(30);
        companyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        companyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        companyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyTable.setShowGrid(true);
        companyTable.setGridColor(new Color(230, 230, 230));

        // Set column widths
        companyTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Symbol
        companyTable.getColumnModel().getColumn(1).setPreferredWidth(250);  // Company
        companyTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Country
        companyTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Market Cap
        companyTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // P/E Ratio

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(companyTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Perform search when user clicks search button or presses Enter.
     */
    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            // Empty search = show all companies
            listController.loadCompanyList();
        } else {
            // Search for matching companies
            searchController.searchCompany(query);
        }
    }

    /**
     * Clear search and show all companies.
     */
    private void clearSearch() {
        searchField.setText("");
        listController.loadCompanyList();
    }

    /**
     * Handle property change events from view models.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("companies".equals(evt.getPropertyName())) {
            // Company list was updated
            updateTable(listViewModel.getCompanies());
        } else if ("searchResults".equals(evt.getPropertyName())) {
            // Search results were updated
            updateTable(searchViewModel.getSearchResults());
        } else if ("error".equals(evt.getPropertyName())) {
            // An error occurred
            String error = listViewModel.getErrorMessage();
            if (error.isEmpty()) {
                error = searchViewModel.getErrorMessage();
            }
            if (!error.isEmpty()) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Update the table with new company data.
     */
    private void updateTable(List<CompanyDisplayData> companies) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add new rows
        for (CompanyDisplayData company : companies) {
            Object[] row = {
                    company.getSymbol(),
                    company.getName(),
                    company.getCountry(),
                    company.getFormattedMarketCap(),
                    company.getFormattedPeRatio()
            };
            tableModel.addRow(row);
        }

        // Update table display
        companyTable.revalidate();
        companyTable.repaint();
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Billionaire Stock Platform");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.add(new CompanyListPage());
            frame.setVisible(true);
        });
    }
}