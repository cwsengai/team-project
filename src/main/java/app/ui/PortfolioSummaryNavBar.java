package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class PortfolioSummaryNavBar extends JPanel {
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:MagicNumber", "checkstyle:TrailingComment", "checkstyle:LambdaParameterName"})
    public PortfolioSummaryNavBar(JFrame frame) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        final JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.BLACK);
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 28, 8, 28));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setFocusable(false);
        backButton.setAlignmentX(JButton.RIGHT_ALIGNMENT);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setMaximumSize(new Dimension(160, 40));
        // Close window for demo
        backButton.addActionListener(pressback -> frame.dispose());

        // Make button pill-shaped
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 0, true),
            BorderFactory.createEmptyBorder(8, 28, 8, 28)
        ));
        backButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        final JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.add(Box.createHorizontalGlue());
        rightPanel.add(backButton);
        add(rightPanel, BorderLayout.EAST);
    }
}
