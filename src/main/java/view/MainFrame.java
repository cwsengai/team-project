package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Main application frame that contains all views as tabs.
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Portfolio Management System");
        initializeComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
    }

    /**
     * Add a tab to the main frame.
     * @param title The title of the tab
     * @param component The component to display in the tab
     */
    public void addTab(String title, java.awt.Component component) {
        tabbedPane.addTab(title, component);
    }

    /**
     * Get the tabbed pane.
     * @return The tabbed pane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
