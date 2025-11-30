// File: src/main/java/framework_and_driver/CompanyListPage.java
package frameworkanddriver;

import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.controller.CompanyListController;
import interfaceadapter.controller.SearchCompanyController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


/**
 * Main page showing company list and search functionality.
 * Implements User Stories 1 (Company List) and 2 (Search).
 */
public class CompanyListPage extends JPanel implements PropertyChangeListener {
    private CompanyListController listController;
    private SearchCompanyController searchController;

    private JTable companyTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // For economic indicator
    private JPanel economicIndicatorsPanel;
    private List<entity.EconomicIndicator> economicIndicators;

    // For market index
    private JPanel marketIndicesPanel;
    private List<entity.MarketIndex> marketIndices;

    // --- Custom Colors for the modern look ---
    private static final Color BACKGROUND_COLOR = new Color(248, 248, 248);
    private static final Color HEADER_BG_COLOR = Color.WHITE;
    private static final Color PRIMARY_TEXT = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(40, 40, 40);
    private static final Color POSITIVE_CHANGE = new Color(0, 150, 0);
    private static final Color NEGATIVE_CHANGE = new Color(200, 0, 0);
    private static final Color TABLE_HEADER_BG = new Color(230, 230, 230);
    private static final Color LINK_COLOR = new Color(133, 165, 168);

    public CompanyListPage() {
        // Just build UI - controllers will be set later
        setupUI();
    }

    private void runInitialDataLoad() {
        // Run data loading in background to prevent UI freeze
        if (listController != null) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    listController.loadCompanyList();
                    return null;
                }
            }.execute();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JScrollPane mainScrollPane = new JScrollPane(createContentPanel());
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the header panel with the title, Trade button, and Sign In button.
     * Fixed: Uses BoxLayout to ensure buttons are always visible on the right.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        // Use BoxLayout (X_AXIS) for robust left-to-right alignment
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(HEADER_BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        // Logo
        JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerPanel.add(logo);

        // --- MIDDLE SPACER ---
        // This glue pushes everything after it to the far right
        headerPanel.add(Box.createHorizontalGlue());

        // --- Right Side: Buttons ---

        // Trade Button
        JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(0, 120, 215));  // Blue color
        tradeButton.setForeground(Color.WHITE);
        tradeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        tradeButton.setFocusPainted(false);
        tradeButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        tradeButton.setOpaque(true); // Ensure color renders on all OS
        tradeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tradeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tradeButton.addActionListener(e -> {
            // ✅ Navigate to SimulateMain
            openSimulatePage();
        });
        headerPanel.add(tradeButton);

        // Add spacer between buttons
        headerPanel.add(Box.createHorizontalStrut(15));

        // Sign In Button
        JButton signInButton = new JButton("Sign In");
        signInButton.setBackground(ACCENT_COLOR);
        signInButton.setForeground(Color.WHITE);
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signInButton.setFocusPainted(false);
        signInButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        signInButton.setOpaque(true); // Ensure color renders on all OS
        signInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInButton.addActionListener(e -> {
            // ✅ Navigate to PortfolioSummaryMain
            openPortfolioSummaryPage();
        });
        headerPanel.add(signInButton);

        return headerPanel;
    }

    /**
     * Create the main content panel which holds all sections.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        // --- Stock Charts/Indices Panel ---
        JPanel chartsPanel = createStockChartsPanel();
        chartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(chartsPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // --- Economic Indicators Panel ---
        JPanel economicPanel = createEconomicIndicatorsPanel();
        economicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(economicPanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // --- Company List Panel ---
        JPanel companyPanel = createCompanyListPanel();
        companyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(companyPanel);

        return contentPanel;
    }

    /**
     * Create the panel for the main stock indices.
     */
    private JPanel createStockChartsPanel() {
        marketIndicesPanel = new JPanel(new GridBagLayout());
        marketIndicesPanel.setBackground(HEADER_BG_COLOR);
        marketIndicesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Initially show loading state
        refreshMarketIndices();

        marketIndicesPanel.setMaximumSize(new Dimension(1000, marketIndicesPanel.getPreferredSize().height));
        marketIndicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return marketIndicesPanel;
    }

    public void setMarketIndices(List<entity.MarketIndex> indices) {
        this.marketIndices = indices;
        SwingUtilities.invokeLater(this::refreshMarketIndices);
    }

    private void refreshMarketIndices() {
        marketIndicesPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Headers
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        marketIndicesPanel.add(createStyledLabel("Market Index", true), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        marketIndicesPanel.add(createStyledLabel("Current Price", true), gbc);

        gbc.gridx = 2;
        marketIndicesPanel.add(createStyledLabel("Change", true), gbc);

        gbc.gridx = 3;
        marketIndicesPanel.add(createStyledLabel("Change %", true), gbc);

        gbc.gridx = 4;  // Added Details column header
        marketIndicesPanel.add(createStyledLabel("Details", true), gbc);

        // Separator
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 5;  // Changed from 4 to 5
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        marketIndicesPanel.add(separator, gbc);

        gbc.gridwidth = 1;

        // Data rows
        if (marketIndices == null || marketIndices.isEmpty()) {
            // Show loading
            int row = 2;
            addMarketIndexRowGB(marketIndicesPanel, gbc, row++, "S&P 500", "Loading...", "...", "...");
            addMarketIndexRowGB(marketIndicesPanel, gbc, row++, "NASDAQ", "Loading...", "...", "...");
            addMarketIndexRowGB(marketIndicesPanel, gbc, row++, "Dow Jones", "Loading...", "...", "...");
        } else {
            // Show real data
            int row = 2;
            for (entity.MarketIndex index : marketIndices) {
                String changeStr = String.format("%+.2f", index.getChange());
                String changeColor = index.isPositive() ? "positive" : "negative";

                addMarketIndexRowGB(marketIndicesPanel, gbc, row++,
                        index.getName(),
                        index.getFormattedPrice(),
                        changeStr,
                        index.getFormattedChangePercent(),
                        changeColor
                );
            }
        }

        marketIndicesPanel.revalidate();
        marketIndicesPanel.repaint();
    }

    private void addMarketIndexRowGB(JPanel panel, GridBagConstraints gbc, int row,
                                     String name, String price, String change, String changePercent) {
        addMarketIndexRowGB(panel, gbc, row, name, price, change, changePercent, "neutral");
    }

    /**
     * Add market index row with color and View Details button.
     */
    private void addMarketIndexRowGB(JPanel panel, GridBagConstraints gbc, int row,
                                     String name, String price, String change, String changePercent, String colorType) {
        gbc.gridy = row;

        // Name
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        panel.add(createStyledLabel(name, false), gbc);

        // Price
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        panel.add(createStyledLabel(price, false), gbc);

        // Change
        gbc.gridx = 2;
        JLabel changeLabel = createStyledLabel(change, false);
        if ("positive".equals(colorType)) {
            changeLabel.setForeground(POSITIVE_CHANGE);
        } else if ("negative".equals(colorType)) {
            changeLabel.setForeground(NEGATIVE_CHANGE);
        }
        panel.add(changeLabel, gbc);

        // Change Percent
        gbc.gridx = 3;
        JLabel changePercentLabel = createStyledLabel(changePercent, false);
        if ("positive".equals(colorType)) {
            changePercentLabel.setForeground(POSITIVE_CHANGE);
        } else if ("negative".equals(colorType)) {
            changePercentLabel.setForeground(NEGATIVE_CHANGE);
        }
        panel.add(changePercentLabel, gbc);

        // View Details Button
        gbc.gridx = 4;
        JButton detailsButton = new JButton("View Details");
        detailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsButton.setBorderPainted(false);
        detailsButton.setContentAreaFilled(false);
        detailsButton.setForeground(LINK_COLOR);
        detailsButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsButton.setHorizontalAlignment(SwingConstants.LEFT);
        detailsButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Viewing details for: " + name + "\n" +
                        "Current Price: " + price + "\n" +
                        "Change: " + change + "\n" +
                        "Change %: " + changePercent));

        panel.add(detailsButton, gbc);
    }

    /**
     * Create economic indicators panel with real data.
     */
    private JPanel createEconomicIndicatorsPanel() {
        economicIndicatorsPanel = new JPanel(new GridBagLayout());
        economicIndicatorsPanel.setBackground(HEADER_BG_COLOR);
        economicIndicatorsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Initially show loading state
        refreshEconomicIndicators();

        economicIndicatorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return economicIndicatorsPanel;
    }

    public void setEconomicIndicators(List<entity.EconomicIndicator> indicators) {
        this.economicIndicators = indicators;
        SwingUtilities.invokeLater(this::refreshEconomicIndicators);
    }

    private void refreshEconomicIndicators() {
        economicIndicatorsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Headers
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        economicIndicatorsPanel.add(createStyledLabel("Indicator", true), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        economicIndicatorsPanel.add(createStyledLabel("Latest Value", true), gbc);

        gbc.gridx = 2;
        economicIndicatorsPanel.add(createStyledLabel("Last Updated", true), gbc);

        gbc.gridx = 3;
        economicIndicatorsPanel.add(createStyledLabel("Details", true), gbc);

        // Separator
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        economicIndicatorsPanel.add(separator, gbc);

        gbc.gridwidth = 1;

        // Data rows
        if (economicIndicators == null || economicIndicators.isEmpty()) {
            // Show loading for all 6 indicators
            int row = 2;
            addIndicatorRowGB(economicIndicatorsPanel, gbc, row++, "Loading economic data...", "...", "...");
        } else {
            // Show real data - ALL of them
            int row = 2;
            for (entity.EconomicIndicator indicator : economicIndicators) {
                addIndicatorRowGB(economicIndicatorsPanel, gbc, row++,
                        indicator.getName(),
                        indicator.getValue(),
                        indicator.getLastUpdated());
            }
        }

        economicIndicatorsPanel.revalidate();
        economicIndicatorsPanel.repaint();

        // Force parent container to update
        if (economicIndicatorsPanel.getParent() != null) {
            economicIndicatorsPanel.getParent().revalidate();
            economicIndicatorsPanel.getParent().repaint();
        }
    }

    private void addIndicatorRowGB(JPanel panel, GridBagConstraints gbc, int row, String name, String value, String date) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        panel.add(createStyledLabel(name, false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        panel.add(createStyledLabel(value, false), gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel(date, false), gbc);

        gbc.gridx = 3;
        JButton detailsButton = new JButton("View Details");
        detailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsButton.setBorderPainted(false);
        detailsButton.setContentAreaFilled(false);
        detailsButton.setForeground(LINK_COLOR);
        detailsButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsButton.setHorizontalAlignment(SwingConstants.LEFT);
        detailsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Viewing details for: " + name));

        panel.add(detailsButton, gbc);
    }

    private JLabel createStyledLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(PRIMARY_TEXT);
        return label;
    }

    /**
     * Create company list panel with search functionality.
     */
    private JPanel createCompanyListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Top 100 Fortune");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        searchField.addActionListener(e -> performSearch());

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        searchButton.addActionListener(e -> performSearch());
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clearButton.setBackground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        clearButton.addActionListener(e -> clearSearch());
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton magnifyingGlassButton = new JButton("🔍");
        magnifyingGlassButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        magnifyingGlassButton.setBackground(Color.WHITE);
        magnifyingGlassButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        magnifyingGlassButton.addActionListener(e -> performSearch());
        magnifyingGlassButton.setFocusPainted(false);
        magnifyingGlassButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(magnifyingGlassButton);

        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Updated columns to show actual API data
        String[] columns = {"Symbol", "Company", "Country", "Market Cap", "P/E Ratio", "View"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;  // Only "View" button is editable
            }
        };

        companyTable = new JTable(tableModel);
        companyTable.setRowHeight(40);
        companyTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        companyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        companyTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        companyTable.getTableHeader().setOpaque(false);
        companyTable.setBackground(HEADER_BG_COLOR);
        companyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyTable.setShowGrid(false);
        companyTable.setIntercellSpacing(new Dimension(0, 0));

        companyTable.setDefaultRenderer(Object.class, new CustomTableRenderer());
        companyTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        companyTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField()));

        // Adjusted column widths for new columns
        companyTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // Symbol
        companyTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Company
        companyTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Country
        companyTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Market Cap
        companyTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // P/E Ratio
        companyTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // View

        JScrollPane scrollPane = new JScrollPane(companyTable);
        scrollPane.setPreferredSize(new Dimension(800, 450));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(HEADER_BG_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(1000, panel.getPreferredSize().height));

        return panel;
    }

    // --- Helper classes for the 'View' button in the table ---
    private class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button;

        public ButtonRenderer() {
            button = new JButton("View");
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(table.getBackground());
            panel.add(button);
            return panel;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;  // ✅ Add this field

        public ButtonEditor(JTextField textField) {
            super(textField);
            setClickCountToStart(1);

            button = new JButton("View");
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;  // ✅ Store the row
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(table.getBackground());
            panel.add(button);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // ✅ Get company symbol from the table
                String symbol = (String) companyTable.getValueAt(currentRow, 0);  // Column 0 is Symbol
                String companyName = (String) companyTable.getValueAt(currentRow, 1);  // Column 1 is Name

                System.out.println("📊 Opening details for: " + symbol + " - " + companyName);

                // ✅ Navigate to CompanyMain
                openCompanyPage(symbol);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    public void setListController(CompanyListController controller) {
        this.listController = controller;
    }

    public void setSearchController(SearchCompanyController controller) {
        this.searchController = controller;
    }

    public void loadInitialData() {
        runInitialDataLoad();
    }

    private void performSearch() {
        if (searchController == null) {
            System.err.println("❌ Search controller is null!");
            JOptionPane.showMessageDialog(this,
                    "Search is not ready yet. Please wait for data to load.",
                    "Search Not Ready",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = searchField.getText().trim();
        System.out.println("🔍 Search triggered with query: '" + query + "'");

        if (query.isEmpty()) {
            System.out.println("🔍 Empty query - showing all companies");
            if (listController != null) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        listController.loadCompanyList();
                        return null;
                    }
                }.execute();
            }
        } else if (query.length() < 2) {
            System.out.println("⚠️ Query too short (less than 2 characters)");
            JOptionPane.showMessageDialog(this,
                    "Please enter at least 2 characters to search.",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("🔍 Executing search for: " + query);
            searchController.searchCompany(query);
        }
    }

    private void clearSearch() {
        System.out.println("🔍 Clearing search");
        searchField.setText("");
        if (listController != null) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    listController.loadCompanyList();
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("companies".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> updateTable((List<CompanyDisplayData>) evt.getNewValue()));
        } else if ("searchResults".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> updateTable((List<CompanyDisplayData>) evt.getNewValue()));
        } else if ("error".equals(evt.getPropertyName())) {
            String error = (String) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                if (error != null && !error.isEmpty()) displayError(error);
            });
        }
    }

    public void updateTable(List<CompanyDisplayData> companies) {
        tableModel.setRowCount(0);
        if (companies != null && !companies.isEmpty()) {
            for (CompanyDisplayData company : companies) {
                Object[] row = {
                        company.getSymbol(),
                        company.getName(),
                        company.getCountry(),
                        company.getFormattedMarketCap(),
                        company.getFormattedPeRatio(),
                        "View"
                };
                tableModel.addRow(row);
            }
        } else {
            Object[] emptyRow = {"", "No companies found", "", "", "", ""};
            tableModel.addRow(emptyRow);
        }
        companyTable.revalidate();
        companyTable.repaint();
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void updateCompanyList(List<CompanyDisplayData> companies) {
        updateTable(companies);
    }


    /**
     * Navigate to the Simulate Trading page.
     * Closes current window and opens SimulateMain.
     */
    private void openSimulatePage() {
        System.out.println("📊 Navigating to Simulate Trading page...");

        // Get the parent frame (window)
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Close current window
        if (parentFrame != null) {
            parentFrame.dispose();
        }

        // Open SimulateMain
        SwingUtilities.invokeLater(() -> {
            try {
                app.SimulatedMain.main(new String[]{});
            } catch (Exception ex) {
                System.err.println("❌ Error opening Simulate page: " + ex.getMessage());
                ex.printStackTrace();

                // Show error dialog
                JOptionPane.showMessageDialog(null,
                        "Failed to open trading simulator: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Navigate to the Company Details page.
     * Closes current window and opens CompanyMain with the selected company.
     *
     * @param symbol The company ticker symbol (e.g., "AAPL", "MSFT")
     */
    private void openCompanyPage(String symbol) {
        System.out.println("📊 Navigating to Company Details page for: " + symbol);

        // Get the parent frame (window)
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Close current window
        if (parentFrame != null) {
            parentFrame.dispose();
        }

        // Open CompanyMain with the selected company
        SwingUtilities.invokeLater(() -> {
            try {
                // Pass the symbol to CompanyMain
                app.CompanyMain.main(new String[]{symbol});
            } catch (Exception ex) {
                System.err.println("❌ Error opening Company Details page: " + ex.getMessage());
                ex.printStackTrace();

                // Show error dialog
                JOptionPane.showMessageDialog(null,
                        "Failed to open company details: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Navigate to the Portfolio Summary page.
     * Keeps current window open and opens PortfolioSummaryMain in a new window.
     */
    private void openPortfolioSummaryPage() {
        System.out.println("👤 Opening Portfolio Summary page...");

        // ✅ Don't close current window - just open new one
        SwingUtilities.invokeLater(() -> {
            try {
                app.PortfolioSummaryMain.main(new String[]{});
            } catch (Exception ex) {
                System.err.println("❌ Error opening Portfolio Summary page: " + ex.getMessage());
                ex.printStackTrace();

                // Show error dialog
                JOptionPane.showMessageDialog(this,
                        "Failed to open portfolio summary: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Custom renderer for table cells to provide alternating row colors and styling.
     */
    private class CustomTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Text color
            c.setForeground(PRIMARY_TEXT);

            // Left-align text
            setHorizontalAlignment(SwingConstants.LEFT);

            // Alternating row colors (makes table easier to read)
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
            } else if (row % 2 == 0) {
                c.setBackground(HEADER_BG_COLOR);           // White
            } else {
                c.setBackground(new Color(250, 250, 250));  // Light gray
            }

            return c;
        }
    }



}