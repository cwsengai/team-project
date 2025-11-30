package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dataaccess.SupabaseTradeDataAccessObject;

public class PortfolioOrderHistoryTable extends JPanel {
    public PortfolioOrderHistoryTable(UUID userId, SupabaseTradeDataAccessObject tradeDAO) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        // Section header
        JLabel sectionHeader = new JLabel("Order History");
        sectionHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        sectionHeader.setForeground(new Color(0x1A, 0x73, 0xE8));
        sectionHeader.setOpaque(true);
        sectionHeader.setBackground(new Color(0xE8, 0xF0, 0xFE));
        sectionHeader.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
        sectionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionHeader.setMaximumSize(new Dimension(200, 36));
        add(sectionHeader, BorderLayout.NORTH);

        // Table data (will be loaded from DB)
        String[] columns = {"Name", "Type", "Amount (USD)", "Purchase Time", "Selling Time", "Purchase Price", "Selling Price", "Profit / PnL", "Return Rate / ROI"};

        Object[][] data;
        try {
            var trades = tradeDAO.fetchTradesForUser(userId);
            data = new Object[trades.size()][columns.length];
            for (int i = 0; i < trades.size(); i++) {
                var t = trades.get(i);
                data[i][0] = t.getTicker();
                data[i][1] = t.isLong() ? "long" : "short";
                data[i][2] = t.getQuantity();
                data[i][3] = t.getEntryTime().toString();
                data[i][4] = t.getExitTime().toString();
                data[i][5] = t.getEntryPrice();
                data[i][6] = t.getExitPrice();
                data[i][7] = t.getRealizedPnL();
                data[i][8] = String.format("%.2f%%", t.getReturnRate());
            }
        } catch (Exception ex) {
            data = new Object[0][columns.length];
        }
        JTable table = new JTable(data, columns);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0xF7, 0xF7, 0xF7));
        table.getTableHeader().setForeground(new Color(0x33, 0x33, 0x33));
        table.setGridColor(new Color(0xEE, 0xEE, 0xEE));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Scroll pane for table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(0xF7, 0xF7, 0xF7));
        scrollPane.setPreferredSize(new Dimension(800, 200));
        add(scrollPane, BorderLayout.CENTER);
    }
}
