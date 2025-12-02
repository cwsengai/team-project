package frameworkanddriver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import entity.EconomicIndicator;
import entity.MarketIndex;
import interfaceadapter.company_list.CompanyDisplayData;
import interfaceadapter.controller.CompanyListController;
import interfaceadapter.controller.SearchCompanyController;

/**
 * Main page showing company list and search functionality.
 * Implements User Stories 1 (Company List) and 2 (Search).
 */
public class CompanyListPage extends JPanel implements PropertyChangeListener {

    // --- Constants for Magic Numbers ---
    private static final int RGB_248 = 248;
    private static final int RGB_40 = 40;
    private static final int RGB_150 = 150;
    private static final int RGB_200 = 200;
    private static final int RGB_230 = 230;
    private static final int RGB_120 = 120;
    private static final int RGB_215 = 215;
    private static final int RGB_133 = 133;
    private static final int RGB_165 = 165;
    private static final int RGB_168 = 168;
    private static final int RGB_220 = 220;
    private static final int RGB_250 = 250;

    private static final int FONT_SIZE_SMALL = 12;
    private static final int FONT_SIZE_MEDIUM = 14;
    private static final int FONT_SIZE_LARGE = 16;
    private static final int FONT_SIZE_XLARGE = 18;

    private static final int PADDING_SMALL = 5;
    private static final int PADDING_MEDIUM = 10;
    private static final int PADDING_LARGE = 15;
    private static final int PADDING_XLARGE = 20;
    private static final int PADDING_XXLARGE = 25;
    private static final int PADDING_30 = 30;

    private static final int ROW_HEIGHT = 40;
    private static final int SCROLL_INCREMENT = 16;
    private static final int SEARCH_FIELD_COLUMNS = 20;

    private static final int COL_WIDTH_SYMBOL = 80;
    private static final int COL_WIDTH_COMPANY = 200;
    private static final int COL_WIDTH_COUNTRY = 120;
    private static final int COL_WIDTH_MARKET_CAP = 120;
    private static final int COL_WIDTH_PE = 100;
    private static final int COL_WIDTH_VIEW = 80;

    private static final int PREF_WIDTH = 800;
    private static final int PREF_HEIGHT = 450;
    private static final int MAX_WIDTH = 1000;

    private static final String FONT_SANS_SERIF = "SansSerif";
    private static final String FONT_ARIAL = "Arial";
    private static final String BUTTON_TEXT_VIEW = "View";

    // --- Custom Colors ---
    private static final Color BACKGROUND_COLOR = new Color(RGB_248, RGB_248, RGB_248);
    private static final Color HEADER_BG_COLOR = Color.WHITE;
    private static final Color PRIMARY_TEXT = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(RGB_40, RGB_40, RGB_40);
    private static final Color POSITIVE_CHANGE = new Color(0, RGB_150, 0);
    private static final Color NEGATIVE_CHANGE = new Color(RGB_200, 0, 0);
    private static final Color TABLE_HEADER_BG = new Color(RGB_230, RGB_230, RGB_230);

    private CompanyListController listController;
    private SearchCompanyController searchController;

    private JTable companyTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // For economic indicator
    private JPanel economicIndicatorsPanel;
    private List<EconomicIndicator> economicIndicators;

    // For market index
    private JPanel marketIndicesPanel;
    private List<MarketIndex> marketIndices;

    /**
     * Constructor for CompanyListPage.
     * Initializes the UI components.
     */
    public CompanyListPage() {
        // Just build UI - controllers will be set later
        setupUI();
    }

    private void runInitialDataLoad() {
        // Run data loading in background to prevent UI freeze
        if (this.listController != null) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    CompanyListPage.this.listController.loadCompanyList();
                    return null;
                }
            }.execute();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        final JScrollPane mainScrollPane = new JScrollPane(createContentPanel());
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        mainScrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the header panel with the title, Trade button, and Sign In button.
     * Uses BoxLayout to ensure buttons are always visible on the right.
     *
     * @return The configured header JPanel.
     */
    private JPanel createHeaderPanel() {
        final JPanel headerPanel = new JPanel();
        // Use BoxLayout (X_AXIS) for robust left-to-right alignment
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(HEADER_BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_30, PADDING_LARGE, PADDING_30));

        // Logo
        final JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_LARGE));
        headerPanel.add(logo);

        // --- MIDDLE SPACER ---
        // This glue pushes everything after it to the far right
        headerPanel.add(Box.createHorizontalGlue());

        // --- Right Side: Buttons ---

        // Trade Button
        final JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(0, RGB_120, RGB_215));
        tradeButton.setForeground(Color.WHITE);
        tradeButton.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_MEDIUM));
        tradeButton.setFocusPainted(false);
        tradeButton.setBorder(BorderFactory.createEmptyBorder(
                PADDING_MEDIUM, PADDING_XXLARGE, PADDING_MEDIUM, PADDING_XXLARGE));
        tradeButton.setOpaque(true);
        tradeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tradeButton.addActionListener(e -> {
            // Navigate to SimulateMain
            openSimulatePage();
        });
        headerPanel.add(tradeButton);

        return headerPanel;
    }

    /**
     * Create the main content panel which holds all sections.
     *
     * @return The main content JPanel.
     */
    private JPanel createContentPanel() {
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, PADDING_30, PADDING_30, PADDING_30));

        // --- Stock Charts/Indices Panel ---
        final JPanel chartsPanel = createStockChartsPanel();
        chartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(chartsPanel);
        contentPanel.add(Box.createVerticalStrut(PADDING_XXLARGE));

        // --- Economic Indicators Panel ---
        final JPanel economicPanel = createEconomicIndicatorsPanel();
        economicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(economicPanel);
        contentPanel.add(Box.createVerticalStrut(PADDING_30));

        // --- Company List Panel ---
        final JPanel companyPanel = createCompanyListPanel();
        companyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(companyPanel);

        return contentPanel;
    }

    /**
     * Create the panel for the main stock indices.
     *
     * @return The stock charts JPanel.
     */
    private JPanel createStockChartsPanel() {
        this.marketIndicesPanel = new JPanel(new GridBagLayout());
        this.marketIndicesPanel.setBackground(HEADER_BG_COLOR);
        this.marketIndicesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(RGB_220, RGB_220, RGB_220)),
                BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_XLARGE, PADDING_LARGE, PADDING_XLARGE)
        ));

        // Initially show loading state
        refreshMarketIndices();

        this.marketIndicesPanel.setMaximumSize(new Dimension(MAX_WIDTH,
                this.marketIndicesPanel.getPreferredSize().height));
        this.marketIndicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return this.marketIndicesPanel;
    }

    /**
     * Sets the market indices list and refreshes the display.
     *
     * @param indices List of MarketIndex entities.
     */
    public void setMarketIndices(List<MarketIndex> indices) {
        this.marketIndices = indices;
        SwingUtilities.invokeLater(this::refreshMarketIndices);
    }

    private void refreshMarketIndices() {
        this.marketIndicesPanel.removeAll();

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, PADDING_SMALL, 8, PADDING_SMALL);

        // Headers
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        this.marketIndicesPanel.add(createStyledLabel("Market Index", true), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        this.marketIndicesPanel.add(createStyledLabel("Current Price", true), gbc);

        gbc.gridx = 2;
        this.marketIndicesPanel.add(createStyledLabel("Change", true), gbc);

        gbc.gridx = 3;
        this.marketIndicesPanel.add(createStyledLabel("Change %", true), gbc);

        // Separator
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 4; // Changed from 5 to 4 (removed Details column)
        final JSeparator separator = new JSeparator();
        separator.setForeground(new Color(RGB_230, RGB_230, RGB_230));
        this.marketIndicesPanel.add(separator, gbc);

        gbc.gridwidth = 1;

        // Data rows
        if (this.marketIndices == null || this.marketIndices.isEmpty()) {
            // Show loading
            int row = 2;
            addMarketIndexRowGb(this.marketIndicesPanel, gbc, row++, "S&P 500",
                    "Loading...", "...", "...");
            addMarketIndexRowGb(this.marketIndicesPanel, gbc, row++, "NASDAQ",
                    "Loading...", "...", "...");
            addMarketIndexRowGb(this.marketIndicesPanel, gbc, row, "Dow Jones",
                    "Loading...", "...", "...");
        } else {
            // Show real data
            int row = 2;
            for (MarketIndex index : this.marketIndices) {
                final String changeStr = String.format("%+.2f", index.getChange());
                final String changeColor = index.isPositive() ? "positive" : "negative";

                addMarketIndexRowGbColor(this.marketIndicesPanel, gbc, row++,
                        index.getName(),
                        index.getFormattedPrice(),
                        changeStr,
                        index.getFormattedChangePercent(),
                        changeColor
                );
            }
        }

        this.marketIndicesPanel.revalidate();
        this.marketIndicesPanel.repaint();
    }

    private void addMarketIndexRowGb(JPanel panel, GridBagConstraints gbc, int row,
                                     String name, String price, String change, String changePercent) {
        addMarketIndexRowGbColor(panel, gbc, row, name, price, change, changePercent, "neutral");
    }

    /**
     * Add market index row with color (View Details button removed).
     */
    private void addMarketIndexRowGbColor(JPanel panel, GridBagConstraints gbc, int row,
                                          String name, String price, String change,
                                          String changePercent, String colorType) {
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
        final JLabel changeLabel = createStyledLabel(change, false);
        if ("positive".equals(colorType)) {
            changeLabel.setForeground(POSITIVE_CHANGE);
        }
        else if ("negative".equals(colorType)) {
            changeLabel.setForeground(NEGATIVE_CHANGE);
        }
        panel.add(changeLabel, gbc);

        // Change Percent
        gbc.gridx = 3;
        final JLabel changePercentLabel = createStyledLabel(changePercent, false);
        if ("positive".equals(colorType)) {
            changePercentLabel.setForeground(POSITIVE_CHANGE);
        }
        else if ("negative".equals(colorType)) {
            changePercentLabel.setForeground(NEGATIVE_CHANGE);
        }
        panel.add(changePercentLabel, gbc);

        // View Details button removed
    }

    /**
     * Create economic indicators panel with real data.
     *
     * @return The economic indicators JPanel.
     */
    private JPanel createEconomicIndicatorsPanel() {
        this.economicIndicatorsPanel = new JPanel(new GridBagLayout());
        this.economicIndicatorsPanel.setBackground(HEADER_BG_COLOR);
        this.economicIndicatorsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(RGB_220, RGB_220, RGB_220)),
                BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_XLARGE, PADDING_LARGE, PADDING_XLARGE)
        ));

        // Initially show loading state
        refreshEconomicIndicators();

        this.economicIndicatorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return this.economicIndicatorsPanel;
    }

    /**
     * Sets the economic indicators list and refreshes the display.
     *
     * @param indicators List of EconomicIndicator entities.
     */
    public void setEconomicIndicators(List<EconomicIndicator> indicators) {
        this.economicIndicators = indicators;
        SwingUtilities.invokeLater(this::refreshEconomicIndicators);
    }

    private void refreshEconomicIndicators() {
        this.economicIndicatorsPanel.removeAll();

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, PADDING_SMALL, 8, PADDING_SMALL);

        // Headers
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        this.economicIndicatorsPanel.add(createStyledLabel("Indicator", true), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        this.economicIndicatorsPanel.add(createStyledLabel("Latest Value", true), gbc);

        gbc.gridx = 2;
        this.economicIndicatorsPanel.add(createStyledLabel("Last Updated", true), gbc);

        // Removed Details column and buttons

        // Separator
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Reduced gridwidth since we removed a column
        final JSeparator separator = new JSeparator();
        separator.setForeground(new Color(RGB_230, RGB_230, RGB_230));
        this.economicIndicatorsPanel.add(separator, gbc);

        gbc.gridwidth = 1;

        // Data rows
        if (this.economicIndicators == null || this.economicIndicators.isEmpty()) {
            // Show loading for all 6 indicators
            int row = 2;
            addIndicatorRowGb(this.economicIndicatorsPanel, gbc, row,
                    "Loading economic data...", "...", "...");
        } else {
            // Show real data
            int row = 2;
            for (EconomicIndicator indicator : this.economicIndicators) {
                addIndicatorRowGb(this.economicIndicatorsPanel, gbc, row++,
                        indicator.getName(),
                        indicator.getValue(),
                        indicator.getLastUpdated());
            }
        }

        this.economicIndicatorsPanel.revalidate();
        this.economicIndicatorsPanel.repaint();

        // Force parent container to update
        if (this.economicIndicatorsPanel.getParent() != null) {
            this.economicIndicatorsPanel.getParent().revalidate();
            this.economicIndicatorsPanel.getParent().repaint();
        }
    }

    private void addIndicatorRowGb(JPanel panel, GridBagConstraints gbc, int row,
                                   String name, String value, String date) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        panel.add(createStyledLabel(name, false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        panel.add(createStyledLabel(value, false), gbc);

        gbc.gridx = 2;
        panel.add(createStyledLabel(date, false), gbc);

        // View Details button removed
    }

    private JLabel createStyledLabel(String text, boolean bold) {
        final JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, FONT_SIZE_MEDIUM));
        label.setForeground(PRIMARY_TEXT);
        return label;
    }

    /**
     * Create company list panel with search functionality.
     *
     * @return The company list JPanel.
     */
    private JPanel createCompanyListPanel() {
        final JPanel panel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        panel.setBackground(BACKGROUND_COLOR);

        final JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        topPanel.setBackground(BACKGROUND_COLOR);

        final JLabel title = new JLabel("Top 100 Fortune");
        title.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_XLARGE));
        topPanel.add(title, BorderLayout.WEST);

        // Search Panel
        final JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);

        this.searchField = new JTextField(SEARCH_FIELD_COLUMNS);
        this.searchField.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM));
        this.searchField.setBorder(BorderFactory.createLineBorder(new Color(RGB_200, RGB_200, RGB_200)));
        this.searchField.addActionListener(e -> performSearch());

        final JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM));
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(RGB_200, RGB_200, RGB_200)));
        searchButton.addActionListener(e -> performSearch());
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM));
        clearButton.setBackground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createLineBorder(new Color(RGB_200, RGB_200, RGB_200)));
        clearButton.addActionListener(e -> clearSearch());
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final JButton magnifyingGlassButton = new JButton("🔍");
        magnifyingGlassButton.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM));
        magnifyingGlassButton.setBackground(Color.WHITE);
        magnifyingGlassButton.setBorder(BorderFactory.createLineBorder(new Color(RGB_200, RGB_200, RGB_200)));
        magnifyingGlassButton.addActionListener(e -> performSearch());
        magnifyingGlassButton.setFocusPainted(false);
        magnifyingGlassButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(this.searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(magnifyingGlassButton);

        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Updated columns to show actual API data
        final String[] columns = {"Symbol", "Company", "Country", "Market Cap", "P/E Ratio", BUTTON_TEXT_VIEW};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        this.companyTable = new JTable(this.tableModel);
        this.companyTable.setRowHeight(ROW_HEIGHT);
        this.companyTable.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM));
        this.companyTable.getTableHeader().setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_MEDIUM));
        this.companyTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        this.companyTable.getTableHeader().setOpaque(false);
        this.companyTable.setBackground(HEADER_BG_COLOR);
        this.companyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.companyTable.setShowGrid(false);
        this.companyTable.setIntercellSpacing(new Dimension(0, 0));

        this.companyTable.setDefaultRenderer(Object.class, new CustomTableRenderer());
        this.companyTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        this.companyTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JTextField()));

        // Adjusted column widths for new columns
        this.companyTable.getColumnModel().getColumn(0).setPreferredWidth(COL_WIDTH_SYMBOL);
        this.companyTable.getColumnModel().getColumn(1).setPreferredWidth(COL_WIDTH_COMPANY);
        this.companyTable.getColumnModel().getColumn(2).setPreferredWidth(COL_WIDTH_COUNTRY);
        this.companyTable.getColumnModel().getColumn(3).setPreferredWidth(COL_WIDTH_MARKET_CAP);
        this.companyTable.getColumnModel().getColumn(4).setPreferredWidth(COL_WIDTH_PE);
        this.companyTable.getColumnModel().getColumn(5).setPreferredWidth(COL_WIDTH_VIEW);

        final JScrollPane scrollPane = new JScrollPane(this.companyTable);
        scrollPane.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(HEADER_BG_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(MAX_WIDTH, panel.getPreferredSize().height));

        return panel;
    }

    // --- Helper classes for the 'View' button in the table ---

    /**
     * Renderer for the button in the JTable.
     */
    private class ButtonRenderer extends DefaultTableCellRenderer {
        private final JButton button;

        ButtonRenderer() {
            this.button = new JButton(BUTTON_TEXT_VIEW);
            this.button.setBackground(ACCENT_COLOR);
            this.button.setForeground(Color.WHITE);
            this.button.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, FONT_SIZE_SMALL));
            this.button.setOpaque(true);
            this.button.setFocusPainted(false);
            this.button.setBorder(BorderFactory.createEmptyBorder(
                    PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            final JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(table.getBackground());
            panel.add(this.button);
            return panel;
        }
    }

    /**
     * Editor for the button in the JTable.
     */
    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        ButtonEditor(JTextField textField) {
            super(textField);
            setClickCountToStart(1);

            this.button = new JButton(BUTTON_TEXT_VIEW);
            this.button.setBackground(ACCENT_COLOR);
            this.button.setForeground(Color.WHITE);
            this.button.setFont(new Font(FONT_ARIAL, Font.BOLD, FONT_SIZE_SMALL));
            this.button.setOpaque(true);
            this.button.setFocusPainted(false);
            this.button.setBorder(BorderFactory.createEmptyBorder(
                    PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));

            this.button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            this.label = value == null ? "" : value.toString();
            this.button.setText(this.label);
            this.isPushed = true;
            this.currentRow = row;
            final JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(table.getBackground());
            panel.add(this.button);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (this.isPushed) {
                // Get company symbol from the table
                final String symbol = (String) CompanyListPage.this.companyTable.getValueAt(this.currentRow, 0);
                // Navigate to CompanyMain
                openCompanyPage(symbol);
            }
            this.isPushed = false;
            return this.label;
        }

        @Override
        public boolean stopCellEditing() {
            this.isPushed = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Sets the list controller.
     *
     * @param controller The CompanyListController.
     */
    public void setListController(CompanyListController controller) {
        this.listController = controller;
    }

    /**
     * Sets the search controller.
     *
     * @param controller The SearchCompanyController.
     */
    public void setSearchController(SearchCompanyController controller) {
        this.searchController = controller;
    }

    /**
     * Loads the initial data for the page.
     */
    public void loadInitialData() {
        runInitialDataLoad();
    }

    private void performSearch() {
        if (this.searchController == null) {
            JOptionPane.showMessageDialog(this,
                    "Search is not ready yet. Please wait for data to load.",
                    "Search Not Ready",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String query = this.searchField.getText().trim();

        if (query.isEmpty()) {
            if (this.listController != null) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        CompanyListPage.this.listController.loadCompanyList();
                        return null;
                    }
                }.execute();
            }
        } else if (query.length() < 2) {
            JOptionPane.showMessageDialog(this,
                    "Please enter at least 2 characters to search.",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.searchController.searchCompany(query);
        }
    }

    private void clearSearch() {
        this.searchField.setText("");
        if (this.listController != null) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    CompanyListPage.this.listController.loadCompanyList();
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("companies".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> updateTable((List<CompanyDisplayData>) evt.getNewValue()));
        }
        else if ("searchResults".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(() -> updateTable((List<CompanyDisplayData>) evt.getNewValue()));
        } else if ("error".equals(evt.getPropertyName())) {
            final String error = (String) evt.getNewValue();
            SwingUtilities.invokeLater(() -> {
                if (error != null && !error.isEmpty()) {
                    displayError(error);
                }
            });
        }
    }

    /**
     * Updates the table with new company data.
     *
     * @param companies List of CompanyDisplayData.
     */
    public void updateTable(List<CompanyDisplayData> companies) {
        this.tableModel.setRowCount(0);
        if (companies != null && !companies.isEmpty()) {
            for (CompanyDisplayData company : companies) {
                final Object[] row = {
                        company.getSymbol(),
                        company.getName(),
                        company.getCountry(),
                        company.getFormattedMarketCap(),
                        company.getFormattedPeRatio(),
                        BUTTON_TEXT_VIEW,
                };
                this.tableModel.addRow(row);
            }
        } else {
            final Object[] emptyRow = {"", "No companies found", "", "", "", ""};
            this.tableModel.addRow(emptyRow);
        }
        this.companyTable.revalidate();
        this.companyTable.repaint();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the company list in the view.
     *
     * @param companies The list of company data.
     */
    public void updateCompanyList(List<CompanyDisplayData> companies) {
        updateTable(companies);
    }

    /**
     * Navigate to the Simulate Trading page.
     * Closes current window and opens SimulateMain.
     */
    private void openSimulatePage() {
        // Get the parent frame (window)
        final JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Close current window
        if (parentFrame != null) {
            parentFrame.dispose();
        }

        // Open SimulateMain
        SwingUtilities.invokeLater(() -> {
            try {
                app.SimulatedMain.main(new String[]{});
            } catch (Exception ex) {
                System.err.println("Failed to open trading simulator: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
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
        // Get the parent frame (window)
        final JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

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
                System.err.println("Failed to open company details: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
                // Show error dialog
                JOptionPane.showMessageDialog(null,
                        "Failed to open company details: " + ex.getMessage(),
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
            final Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            // Text color
            component.setForeground(PRIMARY_TEXT);

            // Left-align text
            setHorizontalAlignment(SwingConstants.LEFT);

            // Alternating row colors (makes table easier to read)
            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
            } else if (row % 2 == 0) {
                component.setBackground(HEADER_BG_COLOR);           // White
            } else {
                component.setBackground(new Color(RGB_250, RGB_250, RGB_250));  // Light gray
            }

            return component;
        }
    }
}
