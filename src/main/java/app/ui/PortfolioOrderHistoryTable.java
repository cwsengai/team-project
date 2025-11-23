package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class PortfolioOrderHistoryTable extends JPanel {
    public PortfolioOrderHistoryTable() {
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

        // Table data (example rows)
        String[] columns = {"Name", "Type", "Amount (USD)", "Purchase Time", "Selling Time", "Purchase Price", "Selling Price", "Profit / PnL", "Return Rate / ROI"};
        Object[][] data = {
                {"Apple", "long", "560,000 usd", "2025-11-05 17:00", "2025-11-05 22:00", "280 usd", "275 usd", "10,000 usd", "17.857%"},
                {"Apple", "short", "266,800 usd", "2025-11-05 17:00", "2025-11-05 22:00", "266.8 usd", "2679 usd", "1,100 usd", "0.412%"},
                {"Apple", "long", "560,000 usd", "2025-11-05 17:00", "2025-11-05 22:00", "280 usd", "275 usd", "10,000 usd", "17.857%"},
                {"Apple", "short", "266,800 usd", "2025-11-05 17:00", "2025-11-05 22:00", "266.8 usd", "2679 usd", "1,100 usd", "0.412%"},
                {"Apple", "long", "560,000 usd", "2025-11-05 17:00", "2025-11-05 22:00", "280 usd", "275 usd", "10,000 usd", "17.857%"},
                {"Apple", "short", "266,800 usd", "2025-11-05 17:00", "2025-11-05 22:00", "266.8 usd", "2679 usd", "1,100 usd", "0.412%"}
        };
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
