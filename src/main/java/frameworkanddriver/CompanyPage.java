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

import interfaceadapter.IntervalController;
import interfaceadapter.controller.CompanyController;
import interfaceadapter.controller.FinancialStatementController;
import interfaceadapter.controller.NewsController;
import interfaceadapter.view_model.CompanyViewModel;
import interfaceadapter.view_model.FinancialStatementViewModel;
import interfaceadapter.view_model.NewsViewModel;

public class CompanyPage extends JFrame {

    // Constant
    public static final int UIWIDTH = 1400;
    public static final int UIHEIGHT = 900;
    public static final int TOPPANEL = 10;
    public static final int PANELLOGO = 16;
    public static final int SEARCHPANEL = 12;
    public static final int MAINPANELWIDTH = 900;
    public static final int MAINPANELHEIGHT = 800;
    public static final int MAINPANELCENTERWIDTH = 400;
    public static final int MAINPANELCENTERHEIGHT = 300;
    public static final int DIVIDERLOCATION = 600;
    public static final double RESIZEWEIGHT = 0.5;
    public static final int BOTTOMWIDTH = 100;
    public static final int BOTTOMHEIGHT = 250;
    public static final int CHARTTITLEFONT = 22;
    public static final int R = 200;
    public static final int G = 200;
    public static final int B = 200;
    public static final int CHARTWIDTH = 1200;
    public static final int CHARTHEIGHT = 700;
    public static final int CHARTMINWIDTH = 1000;
    public static final int CHARTMINHEIGHT = 600;
    public static final int INTERVALBUTTONS = 5;
    public static final int DESCRIPTIONROWS = 6;
    public static final int DESCRIPTIONCOLS = 30;
    // ViewModels
    private final CompanyViewModel companyviewmodel;
    private final FinancialStatementViewModel fsviewmodel;
    private final NewsViewModel newsviewmodel;

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

    public CompanyPage(CompanyViewModel companyviewmodel,
                       FinancialStatementViewModel fsviewmodel,
                       NewsViewModel newsviewmodel) {

        this.companyviewmodel = companyviewmodel;
        this.fsviewmodel = fsviewmodel;
        this.newsviewmodel = newsviewmodel;

        companyviewmodel.setListener(this::refreshCompany);
        fsviewmodel.setListener(this::refreshFinancials);
        newsviewmodel.setListener(this::refreshNews);

        buildui();
    }

    /**
     * Injects all controllers used by this page to handle user actions.
     *
     * @param companyController the controller responsible for company overview requests
     * @param fsController the controller responsible for financial statement requests
     * @param newsController the controller responsible for news requests
     * @param chartController the controller responsible for chart interval updates
     */
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
    private void buildui() {
        setTitle("Stock Analysis Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIWIDTH, UIHEIGHT);
        setLayout(new BorderLayout());

        final JPanel northContainer = new JPanel();
        northContainer.setLayout(new BorderLayout());

        northContainer.add(createTopPanel(), BorderLayout.NORTH);
        northContainer.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(northContainer, BorderLayout.NORTH);

        add(buildMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(TOPPANEL, TOPPANEL, TOPPANEL, TOPPANEL));
        panel.setBackground(Color.WHITE);

        // Logo
        final JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, PANELLOGO));

        // Login button
        final JButton loginButton = new JButton("Signup/ Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.setFocusPainted(false);

        panel.add(logo, BorderLayout.WEST);
        panel.add(loginButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildSearchPanel() {
        final JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        symbolField = new JTextField(SEARCHPANEL);
        final JButton searchButton = new JButton("Search");

        searchButton.addActionListener(gettext -> {
            final String symbol = symbolField.getText().trim();

            if (!symbol.isEmpty()) {
                currentTicker = symbol;
                if (companyController != null) {
                    companyController.onCompanySelected(symbol);
                }
                if (fsController != null) {
                    fsController.onFinancialRequest(symbol);
                }
                if (newsController != null) {
                    newsController.onNewsRequest(symbol);
                }
                if (chartController != null) {
                    chartController.setCurrentTicker(symbol);
                }
            }
        });
        p.add(new JLabel("Symbol: "));
        p.add(symbolField);
        p.add(searchButton);

        return p;
    }

    private JPanel buildMainPanel() {
        final JPanel main = new JPanel(new BorderLayout());

        // LEFT — Chart + intervals
        final JPanel left = buildChartPanel();
        left.setPreferredSize(new Dimension(MAINPANELWIDTH, MAINPANELHEIGHT));
        main.add(left, BorderLayout.WEST);

        // CENTER — Overview
        final JScrollPane overviewScroll = new JScrollPane(buildOverviewPanel());
        overviewScroll.setPreferredSize(new Dimension(MAINPANELCENTERWIDTH, MAINPANELCENTERHEIGHT));
        main.add(overviewScroll, BorderLayout.CENTER);

        // BOTTOM — Financials + News
        final JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(buildFinancialPanel()),
                new JScrollPane(buildNewsPanel())
        );
        bottomSplit.setDividerLocation(DIVIDERLOCATION);
        bottomSplit.setResizeWeight(RESIZEWEIGHT);
        bottomSplit.setPreferredSize(new Dimension(BOTTOMWIDTH, BOTTOMHEIGHT));

        main.add(bottomSplit, BorderLayout.SOUTH);

        return main;
    }

    // ---------------------------------------------------------
    // LEFT SIDE — CHART PANEL (from teammate)
    // ---------------------------------------------------------
    private JPanel buildChartPanel() {

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(TOPPANEL, TOPPANEL, TOPPANEL, TOPPANEL));

        // Header
        final JPanel headerPanel = new JPanel(new BorderLayout());
        final JLabel title = new JLabel("Company Chart", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, CHARTTITLEFONT));

        final JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(R, G, B));
        tradeButton.setFocusPainted(false);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(tradeButton, BorderLayout.EAST);

        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(CHARTWIDTH, CHARTHEIGHT));
        chartPanel.setMinimumSize(new java.awt.Dimension(CHARTMINWIDTH, CHARTMINHEIGHT));

        // Interval buttons
        final JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, INTERVALBUTTONS, INTERVALBUTTONS));
        final JButton btn5min = new JButton("5min");
        final JButton btn1day = new JButton("1 day");
        final JButton btn1week = new JButton("1 week");
        final JButton zoomIn = new JButton("Zoom in");

        btn5min.addActionListener(fivemins -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("5M");
            }
        });
        btn1day.addActionListener(oneday -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1D");
            }
        });
        btn1week.addActionListener(oneweek -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1W");
            }
        });

        intervalPanel.add(btn5min);
        intervalPanel.add(btn1day);
        intervalPanel.add(btn1week);
        intervalPanel.add(zoomIn);

        final JPanel bottom = new JPanel(new BorderLayout());
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
        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Company Overview"));

        nameLabel = new JLabel("Name: ");
        sectorLabel = new JLabel("Sector: ");
        industryLabel = new JLabel("Industry: ");

        descriptionArea = new JTextArea(DESCRIPTIONROWS, DESCRIPTIONCOLS);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);

        errorLabel = new JLabel(" ");
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
        final JPanel p = new JPanel(new BorderLayout());
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
        final JPanel p = new JPanel(new BorderLayout());
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
        if (companyviewmodel.error != null) {
            errorLabel.setText(companyviewmodel.error);
            nameLabel.setText("Name: ");
            sectorLabel.setText("Sector: ");
            industryLabel.setText("Industry: ");
            descriptionArea.setText("");
        }
        else {
            errorLabel.setText("");

            nameLabel.setText("Name: " + companyviewmodel.name);
            sectorLabel.setText("Sector: " + companyviewmodel.sector);
            industryLabel.setText("Industry: " + companyviewmodel.industry);
            descriptionArea.setText(companyviewmodel.description);

            currentTicker = companyviewmodel.symbol;
            if (chartController != null) {
                chartController.setCurrentTicker(currentTicker);
            }
        }
    }

    private void refreshFinancials() {
        fsArea.setText(fsviewmodel.error != null ? "Error: " + fsviewmodel.error : fsviewmodel.formattedOutput);
    }

    private void refreshNews() {
        newsArea.setText(newsviewmodel.error != null ? "Error: " + newsviewmodel.error : newsviewmodel.formattedNews);
    }

    // chart presenter integration:
    /**
     * Updates the chart display using the provided view model.
     *
     * @param viewmodel the data model containing the chart information to render
     */
    public void updateChart(entity.ChartViewModel viewmodel) {
        chartPanel.updateChart(viewmodel);
    }

    /**
     * Displays an error message to the user in a dialog window.
     *
     * @param message the error message to show
     */
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
