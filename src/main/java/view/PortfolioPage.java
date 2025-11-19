package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import interface_adapter.controller.PortfolioController;
import interface_adapter.view.PortfolioView;
import interface_adapter.view_model.PortfolioViewModel;
import interface_adapter.view_model.PositionView;

/**
 * Swing UI page for displaying portfolio information.
 * Implements PortfolioView interface and depends on PortfolioController abstraction.
 */
public class PortfolioPage extends JPanel implements PortfolioView {
    private PortfolioController controller;
    
    // UI Components
    private JLabel portfolioIdLabel;
    private JLabel realizedGainLabel;
    private JLabel unrealizedGainLabel;
    private JLabel totalGainLabel;
    private JLabel snapshotTimeLabel;
    private JTable positionsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    // Current state
    private final String currentPortfolioId;
    private final String currentUserId;

    public PortfolioPage(PortfolioController controller, String portfolioId, String userId) {
        this.controller = controller;
        this.currentPortfolioId = portfolioId;
        this.currentUserId = userId;
        
        initializeComponents();
        layoutComponents();
    }

    /**
     * Set the controller for this view.
     * Allows dependency injection after construction to break circular dependencies.
     * @param controller The portfolio controller
     */
    public void setController(PortfolioController controller) {
        this.controller = controller;
    }

    /**
     * Initialize UI components.
     */
    private void initializeComponents() {
        portfolioIdLabel = new JLabel("Portfolio: Loading...");
        portfolioIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        realizedGainLabel = new JLabel("Realized Gain: $0.00");
        unrealizedGainLabel = new JLabel("Unrealized Gain: $0.00");
        totalGainLabel = new JLabel("Total Gain: $0.00");
        totalGainLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        snapshotTimeLabel = new JLabel("Last Updated: N/A");
        snapshotTimeLabel.setFont(new Font("Arial", Font.ITALIC, 10));

        // Create table for positions
        String[] columnNames = {"Ticker", "Quantity", "Avg Cost", "Market Price", "Market Value", "Unrealized Gain"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        positionsTable = new JTable(tableModel);
        positionsTable.setFillsViewportHeight(true);

        // Create refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> onRefreshClicked());
    }

    /**
     * Layout UI components.
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(portfolioIdLabel, BorderLayout.WEST);
        headerPanel.add(snapshotTimeLabel, BorderLayout.EAST);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.add(realizedGainLabel);
        summaryPanel.add(unrealizedGainLabel);
        summaryPanel.add(totalGainLabel);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        // Top panel combining header and summary
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(summaryPanel, BorderLayout.CENTER);

        // Table panel
        JScrollPane scrollPane = new JScrollPane(positionsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Positions"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);

        // Add all panels to main layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Render portfolio data in the UI.
     */
    @Override
    public void renderPortfolio(PortfolioViewModel viewModel) {
        // Update labels
        portfolioIdLabel.setText("Portfolio: " + viewModel.getPortfolioId());
        realizedGainLabel.setText("Realized Gain: " + viewModel.getFormattedRealizedGain());
        unrealizedGainLabel.setText("Unrealized Gain: " + viewModel.getFormattedUnrealizedGain());
        totalGainLabel.setText("Total Gain: " + String.format("$%.2f", viewModel.getTotalGain()));
        snapshotTimeLabel.setText("Last Updated: " + viewModel.getFormattedSnapshotTime());

        // Set color based on gain/loss
        Color gainColor = viewModel.getTotalGain() >= 0 ? new Color(0, 128, 0) : Color.RED;
        totalGainLabel.setForeground(gainColor);

        // Update table
        tableModel.setRowCount(0); // Clear existing rows
        for (PositionView position : viewModel.getPositions()) {
            Object[] row = {
                    position.getTicker(),
                    position.getQuantity(),
                    String.format("$%.2f", position.getAverageCost()),
                    String.format("$%.2f", position.getMarketPrice()),
                    position.getFormattedMarketValue(),
                    position.getFormattedGain()
            };
            tableModel.addRow(row);
        }

        // TODO: Add row coloring based on gain/loss
        // TODO: Add sorting capability to table columns
    }

    /**
     * Show error message to user.
     */
    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Handle refresh button click.
     */
    public void onRefreshClicked() {
        // TODO: Add loading indicator
        controller.viewPortfolio(currentPortfolioId, currentUserId);
    }

}
