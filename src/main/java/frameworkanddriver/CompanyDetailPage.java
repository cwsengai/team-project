package frameworkanddriver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import entity.CompanyDetailViewModel;
import entity.NewsArticle;
import interfaceadapter.IntervalController;

/**
 * Company detail page UI component that displays company information,
 * stock chart, financial statements, and related news.
 */
public class CompanyDetailPage extends JFrame {

    private ChartPanel chartPanel;
    private JLabel companyNameLabel;
    private JTextArea companyOverviewArea;
    private JPanel newsPanel;
    private JPanel financialStatementsPanel;
    private IntervalController chartController;
    private String currentTicker;

    public CompanyDetailPage() {
        super("Company Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLayout(new BorderLayout());

        // Top panel with header
        add(createTopPanel(), BorderLayout.NORTH);

        // Main content area
        JPanel mainContent = new JPanel(new BorderLayout());
        JPanel leftPanel = createLeftPanel();
        leftPanel.setPreferredSize(new java.awt.Dimension(900, 0));
        mainContent.add(leftPanel, BorderLayout.CENTER);
        mainContent.add(createRightPanel(), BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        // Bottom panel with controls
        add(createBottomPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Logo
        JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Login button
        JButton loginButton = new JButton("Signup/ Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.setFocusPainted(false);

        panel.add(logo, BorderLayout.WEST);
        panel.add(loginButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Company name and trade button
        JPanel headerPanel = new JPanel(new BorderLayout());
        companyNameLabel = new JLabel("##Company Name", SwingConstants.LEFT);
        companyNameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(200, 200, 200));
        tradeButton.setFocusPainted(false);

        headerPanel.add(companyNameLabel, BorderLayout.WEST);
        headerPanel.add(tradeButton, BorderLayout.EAST);

        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 700));
        chartPanel.setMinimumSize(new java.awt.Dimension(1000, 600));

        // Time interval buttons
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

        intervalPanel.add(btn5min);
        intervalPanel.add(btn1day);
        intervalPanel.add(btn1week);
        intervalPanel.add(zoomIn);

        // Combine price panel and interval panel
        JPanel bottomControlPanel = new JPanel(new BorderLayout());
        bottomControlPanel.add(intervalPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(bottomControlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new java.awt.Dimension(300, 0));

        // Company Overview
        JPanel overviewPanel = new JPanel(new BorderLayout());
        overviewPanel.setBorder(BorderFactory.createTitledBorder("Company Overview"));
        overviewPanel.setBackground(new Color(240, 240, 240));
        companyOverviewArea = new JTextArea("##Company Overview");
        companyOverviewArea.setEditable(false);
        companyOverviewArea.setLineWrap(true);
        companyOverviewArea.setWrapStyleWord(true);
        companyOverviewArea.setBackground(new Color(240, 240, 240));
        overviewPanel.add(new JScrollPane(companyOverviewArea), BorderLayout.CENTER);

        // Top 100 list (placeholder)
        JPanel top100Panel = new JPanel(new BorderLayout());
        top100Panel.setBorder(BorderFactory.createTitledBorder("Top 100"));
        JLabel top100Label = new JLabel("Symbol 100");
        top100Panel.add(top100Label, BorderLayout.NORTH);

        panel.add(overviewPanel, BorderLayout.NORTH);
        panel.add(top100Panel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new java.awt.Dimension(0, 200)); // Limit bottom panel height

        // Related News
        JPanel newsPanel = new JPanel(new BorderLayout());
        newsPanel.setBorder(BorderFactory.createTitledBorder("Related News"));
        newsPanel.setPreferredSize(new java.awt.Dimension(600, 0));
        this.newsPanel = new JPanel();
        this.newsPanel.setLayout(new BoxLayout(this.newsPanel, BoxLayout.Y_AXIS));
        newsPanel.add(new JScrollPane(this.newsPanel), BorderLayout.CENTER);

        // Financial Statements
        financialStatementsPanel = new JPanel();
        financialStatementsPanel.setBorder(BorderFactory.createTitledBorder("Financial Statements"));
        financialStatementsPanel.setPreferredSize(new java.awt.Dimension(300, 0));
        financialStatementsPanel.setLayout(new BoxLayout(financialStatementsPanel, BoxLayout.Y_AXIS));
        JLabel incomeStmt = new JLabel("• Income statement");
        JLabel balanceSheet = new JLabel("• Balance Sheet");
        JLabel cashFlow = new JLabel("• CashFlow");
        financialStatementsPanel.add(incomeStmt);
        financialStatementsPanel.add(balanceSheet);
        financialStatementsPanel.add(cashFlow);

        panel.add(newsPanel, BorderLayout.WEST);
        panel.add(financialStatementsPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Update company details UI with view model data
     */
    public void updateCompanyDetails(CompanyDetailViewModel viewModel) {
        if (viewModel == null) {
            return;
        }

        // Update company name
        companyNameLabel.setText(viewModel.getName());

        // Update company overview
        StringBuilder overview = new StringBuilder();
        overview.append("Ticker: ").append(viewModel.getTicker()).append("\n");
        overview.append("Sector: ").append(viewModel.getSector()).append("\n");
        overview.append("Market Cap: ").append(viewModel.getMarketCapFormatted()).append("\n");
        overview.append("P/E Ratio: ").append(viewModel.getPeRatioFormatted()).append("\n");
        overview.append("Latest Revenue: ").append(viewModel.getLatestRevenue()).append("\n");
        overview.append("Latest Net Income: ").append(viewModel.getLatestNetIncome()).append("\n");
        companyOverviewArea.setText(overview.toString());

        // Update news
        updateNews(viewModel.getRecentNews());

        // Store ticker for chart updates
        this.currentTicker = viewModel.getTicker();
        
        // Update chart controller with new ticker
        if (chartController != null) {
            chartController.setCurrentTicker(this.currentTicker);
        }
    }

    private void updateNews(java.util.List<NewsArticle> news) {
        newsPanel.removeAll();
        if (news != null && !news.isEmpty()) {
            for (NewsArticle article : news) {
                JLabel newsItem = new JLabel("• " + article.getTitle());
                newsItem.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                newsPanel.add(newsItem);
            }
        } else {
            JLabel noNews = new JLabel("No recent news available");
            newsPanel.add(noNews);
        }
        newsPanel.revalidate();
        newsPanel.repaint();
    }

    /**
     * Update the chart with new data
     */
    public void updateChart(entity.ChartViewModel chartViewModel) {
        if (chartPanel != null) {
            chartPanel.updateChart(chartViewModel);
        }
    }

    /**
     * Set the chart controller for time interval changes
     */
    public void setChartController(IntervalController controller) {
        this.chartController = controller;
    }

    /**
     * Display error message
     */
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Get the chart panel for error display
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
