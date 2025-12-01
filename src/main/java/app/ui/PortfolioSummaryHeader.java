package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PortfolioSummaryHeader extends JPanel {
    public PortfolioSummaryHeader() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Logo (starburst/asterisk icon)
        final JLabel logo = new JLabel("✶");
        logo.setFont(new Font("SansSerif", Font.BOLD, 32));
        logo.setForeground(Color.BLACK);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        // Brand name
        final JLabel brand = new JLabel("BILLIONAIRE");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.BLACK);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        final JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(logo);
        leftPanel.add(brand);

        add(leftPanel, BorderLayout.WEST);
    }
}
