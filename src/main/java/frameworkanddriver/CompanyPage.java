package frameworkanddriver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import interfaceadapter.controller.CompanyController;
import interfaceadapter.controller.FinancialStatementController;
import interfaceadapter.controller.IntervalController;
import interfaceadapter.controller.NewsController;
import interfaceadapter.view_model.CompanyViewModel;
import interfaceadapter.view_model.FinancialStatementViewModel;
import interfaceadapter.view_model.NewsViewModel;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings({"checkstyle:RegexpMultiline", "checkstyle:ClassDataAbstractionCoupling", "checkstyle:LineLength", "checkstyle:SuppressWarnings"})
public class CompanyPage extends JFrame {

    // ViewModels
    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
    private final CompanyViewModel companyVM;
    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
    private final FinancialStatementViewModel fsVM;
    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:HiddenField", "checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:MagicNumber", "checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:RegexpSinglelineJava", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:NeedBraces", "checkstyle:FinalLocalVariable", "checkstyle:ReturnCount", "checkstyle:LambdaBodyLength", "checkstyle:LambdaParameterName", "checkstyle:MagicNumber", "checkstyle:SuppressWarnings"})
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

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
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
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:TrailingComment", "checkstyle:LambdaParameterName", "checkstyle:VariableDeclarationUsageDistance", "checkstyle:MagicNumber", "checkstyle:ParenPad", "checkstyle:ExecutableStatementCount", "checkstyle:SuppressWarnings", "checkstyle:LineLength"})
    private JPanel buildChartPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        final JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Company Chart", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(200, 200, 200));
        tradeButton.setFocusPainted(false);
        currentTicker = companyVM.getSymbol();
        tradeButton.addActionListener(ex -> enterTradingPage(currentTicker));

        JButton backButton = new JButton("← Back");
        backButton.setBackground(new Color(200, 200, 200));
        backButton.setFocusPainted(false);
        backButton.addActionListener(ex -> backMainPage());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(tradeButton);
        buttonPanel.add(backButton);


        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(1200, 700));
        chartPanel.setMinimumSize(new java.awt.Dimension(1000, 600));

        // Interval buttons
        final JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btn5min = new JButton("5min");
        JButton btn1day = new JButton("1 day");
        JButton btn1week = new JButton("1 week");
        final JButton zoomIn = new JButton("Zoom in");

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
                chartPanel.performZoom();
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
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
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
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
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
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
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
    @SuppressWarnings({"checkstyle:TrailingComment", "checkstyle:ReturnCount", "checkstyle:SuppressWarnings"})
    private void refreshCompany() {
        // 1. Error handling (keep unchanged)
        if (companyVM.getError() != null) {
            errorLabel.setText(companyVM.getError());
            return;
        }

        errorLabel.setText("");

        // 2. Update text information (keep unchanged)
        nameLabel.setText("Name: " + companyVM.getName());
        sectorLabel.setText("Sector: " + companyVM.getSector());
        industryLabel.setText("Industry: " + companyVM.getIndustry());
        descriptionArea.setText(companyVM.getDescription());

        // 3. Update current Ticker
        currentTicker = companyVM.getSymbol();

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
            System.out.println("Auto refreshing chart data: " + currentTicker);
            chartController.handleTimeChange("1D");
        }
        // --- Core modification end ---
    }

    @SuppressWarnings({"checkstyle:AvoidInlineConditionals", "checkstyle:SuppressWarnings"})
    private void refreshFinancials() {
        fsArea.setText(fsVM.getError() != null ? "Error: " + fsVM.getError() : fsVM.getFormattedOutput());
    }

    @SuppressWarnings({"checkstyle:AvoidInlineConditionals", "checkstyle:SuppressWarnings"})
    private void refreshNews() {
        newsArea.setText(newsVM.getError() != null ? "Error: " + newsVM.getError() : newsVM.getFormattedNews());
    }

    // chart presenter integration:
    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public void updateChart(entity.ChartViewModel vm) {
        chartPanel.updateChart(vm);
    }

    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    @SuppressWarnings({"checkstyle:NeedBraces", "checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public void setInitialSymbol(String symbol) {
        symbolField.setText(symbol);

        if (companyController != null) companyController.onCompanySelected(symbol);
        if (fsController != null) fsController.onFinancialRequest(symbol);
        if (newsController != null) newsController.onNewsRequest(symbol);
        if (chartController != null) chartController.setCurrentTicker(symbol);
    }

    @SuppressWarnings({"checkstyle:IllegalCatch", "checkstyle:FinalLocalVariable", "checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public void enterTradingPage(String symbol) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (parentFrame != null) {
            parentFrame.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                app.SimulatedMain.main(new String[] {symbol});
            }
            catch (Exception ex) {
                System.err.println("Error launching simulated trading: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
            }
        });
    }

    public void backMainPage() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                app.CompanyListMain.main(new String[] {});
            }
            catch (Exception ex) {
                System.err.println("Error launching main page: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
            }
        });
    }
}
