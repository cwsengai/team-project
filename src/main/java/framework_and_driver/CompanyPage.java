package framework_and_driver;

import interface_adapter.controller.CompanyController;
import interface_adapter.controller.FinancialStatementController;
import interface_adapter.controller.NewsController;

import interface_adapter.view_model.CompanyViewModel;
import interface_adapter.view_model.FinancialStatementViewModel;
import interface_adapter.view_model.NewsViewModel;

import interface_adapter.controller.IntervalController;

import javax.swing.*;
import java.awt.*;

public class CompanyPage extends JFrame {

    // ViewModels
    private final CompanyViewModel companyVM;
    private final FinancialStatementViewModel fsVM;
    private final NewsViewModel newsVM;

    // Controllers
    private CompanyController companyController;
    private FinancialStatementController fsController;
    private NewsController newsController;
    private IntervalController chartController;

    // UI (search)
    private JTextField symbolField;

    // Chart section
    private ChartPanel chartPanel;
    private JLabel priceLabel;
    private JLabel changeLabel;
    private String currentTicker;

    // Overview section
    private JLabel nameLabel;
    private JLabel sectorLabel;
    private JLabel industryLabel;
    private JTextArea descriptionArea;
    private JLabel errorLabel;

    // Financials + News
    private JTextArea fsArea;
    private JTextArea newsArea;

    public CompanyPage(CompanyViewModel companyVM,
                       FinancialStatementViewModel fsVM,
                       NewsViewModel newsVM) {

        this.companyVM = companyVM;
        this.fsVM = fsVM;
        this.newsVM = newsVM;

        companyVM.setListener(this::refreshCompany);
        fsVM.setListener(this::refreshFinancials);
        newsVM.setListener(this::refreshNews);

        buildUI();
    }

    public void setControllers(CompanyController companyController,
                               FinancialStatementController fsController,
                               NewsController newsController,
                               IntervalController chartController) {
        this.companyController = companyController;
        this.fsController = fsController;
        this.newsController = newsController;
        this.chartController = chartController;
    }

    // ---------------------------------------------------------
    // UI BUILDING
    // ---------------------------------------------------------
    private void buildUI() {
        setTitle("Stock Analysis Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLayout(new BorderLayout());

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BorderLayout());

        northContainer.add(createTopPanel(), BorderLayout.NORTH);
        northContainer.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(northContainer, BorderLayout.NORTH);

        add(buildMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Logo
        JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));

        panel.add(logo, BorderLayout.WEST);

        return panel;
    }

    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        symbolField = new JTextField(12);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String symbol = symbolField.getText().trim();
            if (symbol.isEmpty()) return;

            currentTicker = symbol;

            if (companyController != null) companyController.onCompanySelected(symbol);
            if (fsController != null) fsController.onFinancialRequest(symbol);
            if (newsController != null) newsController.onNewsRequest(symbol);
            if (chartController != null) chartController.setCurrentTicker(symbol);
        });

        p.add(new JLabel("Symbol: "));
        p.add(symbolField);
        p.add(searchButton);

        return p;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());

        // LEFT — Chart + intervals
        JPanel left = buildChartPanel();
        left.setPreferredSize(new Dimension(900, 800));
        main.add(left, BorderLayout.WEST);

        // CENTER — Overview
        JScrollPane overviewScroll = new JScrollPane(buildOverviewPanel());
        overviewScroll.setPreferredSize(new Dimension(400, 300));
        main.add(overviewScroll, BorderLayout.CENTER);

        // BOTTOM — Financials + News
        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(buildFinancialPanel()),
                new JScrollPane(buildNewsPanel())
        );
        bottomSplit.setDividerLocation(600);
        bottomSplit.setResizeWeight(0.5);
        bottomSplit.setPreferredSize(new Dimension(100, 250));

        main.add(bottomSplit, BorderLayout.SOUTH);

        return main;
    }

    // ---------------------------------------------------------
    // LEFT SIDE — CHART PANEL (from teammate)
    // ---------------------------------------------------------
    private JPanel buildChartPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Company Chart", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(200, 200, 200));
        tradeButton.setFocusPainted(false);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(tradeButton, BorderLayout.EAST);

        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(1200, 700));
        chartPanel.setMinimumSize(new java.awt.Dimension(1000, 600));

        // Interval buttons
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btn5min = new JButton("5min");
        JButton btn1day = new JButton("1 day");
        JButton btn1week = new JButton("1 week");
        JButton zoomIn = new JButton("Zoom in");

        btn5min.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("5M");
            }
        });
        btn1day.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1D");
            }
        });
        btn1week.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1W");
            }
        });

        // Bind Zoom In button event
        zoomIn.addActionListener(e -> {
            if (chartPanel != null) {
                chartPanel.performZoom(); // Call the public method we wrote in ChartPanel
            }
        });

        intervalPanel.add(btn5min);
        intervalPanel.add(btn1day);
        intervalPanel.add(btn1week);
        intervalPanel.add(zoomIn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(intervalPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------------------------------------------------
    // OVERVIEW PANEL
    // ---------------------------------------------------------
    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Company Overview"));

        nameLabel = new JLabel("Name: ");
        sectorLabel = new JLabel("Sector: ");
        industryLabel = new JLabel("Industry: ");

        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        p.add(nameLabel);
        p.add(sectorLabel);
        p.add(industryLabel);
        p.add(new JScrollPane(descriptionArea));
        p.add(errorLabel);

        return p;
    }

    // ---------------------------------------------------------
    // FINANCIAL PANEL
    // ---------------------------------------------------------
    private JPanel buildFinancialPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Financial Statements"));

        fsArea = new JTextArea();
        fsArea.setEditable(false);

        p.add(new JScrollPane(fsArea), BorderLayout.CENTER);
        return p;
    }

    // ---------------------------------------------------------
    // NEWS PANEL
    // ---------------------------------------------------------
    private JPanel buildNewsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Latest News"));

        newsArea = new JTextArea();
        newsArea.setEditable(false);

        p.add(new JScrollPane(newsArea), BorderLayout.CENTER);
        return p;
    }

    // ---------------------------------------------------------
    // REFRESH METHODS
    // ---------------------------------------------------------
    private void refreshCompany() {
        // 1. Error handling (keep unchanged)
        if (companyVM.error != null) {
            errorLabel.setText(companyVM.error);
            return;
        }

        errorLabel.setText("");

        // 2. Update text information (keep unchanged)
        nameLabel.setText("Name: " + companyVM.name);
        sectorLabel.setText("Sector: " + companyVM.sector);
        industryLabel.setText("Industry: " + companyVM.industry);
        descriptionArea.setText(companyVM.description);

        // 3. Update current Ticker
        currentTicker = companyVM.symbol;
        
        // --- Core modification start ---
        if (chartController != null) {
            // Step 1: Tell controller which stock is current
            chartController.setCurrentTicker(currentTicker);
            
            // Step 2: Tell chart component to enable Zoom functionality
            if (chartPanel != null) {
                chartPanel.enableZoom(currentTicker);
            }

            // Step 3: [Critical!] Force refresh data once (simulate clicking "1 day")
            // Without this line, the chart will never update!
            System.out.println("Auto refreshing chart data: " + currentTicker); // Debug
            chartController.handleTimeChange("1D");
        }
        // --- Core modification end ---
    }

    private void refreshFinancials() {
        fsArea.setText(fsVM.error != null ? "Error: " + fsVM.error : fsVM.formattedOutput);
    }

    private void refreshNews() {
        newsArea.setText(newsVM.error != null ? "Error: " + newsVM.error : newsVM.formattedNews);
    }

    // chart presenter integration:
    public void updateChart(entity.ChartViewModel vm) {
        chartPanel.updateChart(vm);
    }

    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }


}




