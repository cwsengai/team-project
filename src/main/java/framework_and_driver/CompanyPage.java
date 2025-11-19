package framework_and_driver;

import interface_adapter.controller.CompanyController;
import interface_adapter.controller.FinancialStatementController;
import interface_adapter.controller.NewsController;

import interface_adapter.view_model.CompanyViewModel;
import interface_adapter.view_model.FinancialStatementViewModel;
import interface_adapter.view_model.NewsViewModel;

import javax.swing.*;
import java.awt.*;

public class CompanyPage extends JFrame {

    // ViewModels
    private final CompanyViewModel companyVM;
    private final FinancialStatementViewModel fsVM;
    private final NewsViewModel newsVM;

    // Controllers (assigned later)
    private CompanyController companyController;
    private FinancialStatementController fsController;
    private NewsController newsController;

    // UI Components
    private JTextField symbolField;
    private JButton searchButton;

    private JLabel nameLabel;
    private JLabel sectorLabel;
    private JLabel industryLabel;
    private JTextArea descriptionArea;
    private JLabel errorLabel;

    private JTextArea fsArea;
    private JTextArea newsArea;

    public CompanyPage(CompanyViewModel companyVM,
                       FinancialStatementViewModel fsVM,
                       NewsViewModel newsVM) {

        this.companyVM = companyVM;
        this.fsVM = fsVM;
        this.newsVM = newsVM;

        // Add listeners
        companyVM.setListener(this::refreshCompany);
        fsVM.setListener(this::refreshFinancials);
        newsVM.setListener(this::refreshNews);

        buildUI();
    }

    // Inject controllers after creation
    public void setControllers(CompanyController companyController,
                               FinancialStatementController fsController,
                               NewsController newsController) {
        this.companyController = companyController;
        this.fsController = fsController;
        this.newsController = newsController;
    }

    private void buildUI() {
        setTitle("Stock Analysis Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLayout(new BorderLayout());

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        symbolField = new JTextField(12);
        searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String symbol = symbolField.getText().trim();
            if (symbol.isEmpty()) return;

            // Fire all use cases
            companyController.onCompanySelected(symbol);
            fsController.onFinancialRequest(symbol);
            newsController.onNewsRequest(symbol);
        });

        p.add(new JLabel("Symbol: "));
        p.add(symbolField);
        p.add(searchButton);

        return p;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());

        // ---------- Graph Placeholder Panel (LEFT) ----------
        JPanel graphPlaceholder = new JPanel();
        graphPlaceholder.setPreferredSize(new Dimension(250, 400));
        graphPlaceholder.setBorder(BorderFactory.createTitledBorder("Graph (Coming Soon)"));
        main.add(graphPlaceholder, BorderLayout.WEST);

        // ---------- Scrollable Overview (CENTER) ----------
        JScrollPane overviewScroll = new JScrollPane(buildOverviewPanel());
        overviewScroll.setPreferredSize(new Dimension(100, 250));
        main.add(overviewScroll, BorderLayout.CENTER);

        // ---------- Financial + News Split Bottom (SOUTH) ----------
        JScrollPane financialScroll = new JScrollPane(buildFinancialPanel());
        JScrollPane newsScroll = new JScrollPane(buildNewsPanel());

        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                financialScroll,
                newsScroll
        );
        bottomSplit.setDividerLocation(500);
        bottomSplit.setResizeWeight(0.5);
        bottomSplit.setPreferredSize(new Dimension(100, 300));

        main.add(bottomSplit, BorderLayout.SOUTH);

        return main;
    }


    private JPanel buildOverviewPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(350, 400));
        p.setBorder(BorderFactory.createTitledBorder("Company Overview"));

        nameLabel = new JLabel("Name: ");
        sectorLabel = new JLabel("Sector: ");
        industryLabel = new JLabel("Industry: ");

        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        p.add(nameLabel);
        p.add(sectorLabel);
        p.add(industryLabel);
        p.add(new JScrollPane(descriptionArea));
        p.add(errorLabel);

        return p;
    }

    private JPanel buildFinancialPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Financial Statements"));

        fsArea = new JTextArea();
        fsArea.setEditable(false);

        p.add(new JScrollPane(fsArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildNewsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Latest News"));

        newsArea = new JTextArea();
        newsArea.setEditable(false);

        p.add(new JScrollPane(newsArea), BorderLayout.CENTER);
        return p;
    }

    // ----- REFRESH METHODS -----

    private void refreshCompany() {
        if (companyVM.error != null) {
            errorLabel.setText(companyVM.error);
            return;
        }

        errorLabel.setText("");

        nameLabel.setText("Name: " + companyVM.name);
        sectorLabel.setText("Sector: " + companyVM.sector);
        industryLabel.setText("Industry: " + companyVM.industry);
        descriptionArea.setText(companyVM.description);
    }

    private void refreshFinancials() {
        if (fsVM.error != null) {
            fsArea.setText("Error: " + fsVM.error);
            return;
        }
        fsArea.setText(fsVM.formattedOutput);
    }

    private void refreshNews() {
        if (newsVM.error != null) {
            newsArea.setText("Error: " + newsVM.error);
            return;
        }
        newsArea.setText(newsVM.formattedNews);
    }
}

