package app.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import usecase.auth.AuthService;
import usecase.session.SessionDataAccessInterface;

public class LoginPage extends JDialog {

    private boolean success = false;
    private final SessionDataAccessInterface sessionDAO;

    // Main panels
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public LoginPage(JFrame parent, SessionDataAccessInterface sessionDAO) {
        super(parent, "Billionaire — Login / Signup", true);
        this.sessionDAO = sessionDAO;

        setSize(420, 360);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // === Top Buttons ===
        final JButton loginTab = new JButton("Login");
        final JButton signupTab = new JButton("Sign Up");

        loginTab.setFocusable(false);
        signupTab.setFocusable(false);

        final JPanel tabPanel = new JPanel(new GridLayout(1, 2));
        tabPanel.add(loginTab);
        tabPanel.add(signupTab);

        // === Card Layout ===
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panels
        mainPanel.add(buildLoginPanel(), "login");
        mainPanel.add(buildSignupPanel(), "signup");

        // === Dialog Layout ===
        setLayout(new BorderLayout());
        add(tabPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Tab switching
        loginTab.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        signupTab.addActionListener(e -> cardLayout.show(mainPanel, "signup"));
    }

    // ===============================================================
    // LOGIN PANEL
    // ===============================================================
    private JPanel buildLoginPanel() {
        final JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        final JLabel title = new JLabel("Log In", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        final JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        final JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        final JLabel status = new JLabel("", SwingConstants.CENTER);
        status.setForeground(Color.RED);

        final JButton loginBtn = new JButton("Log In");

        loginBtn.addActionListener((ActionEvent presslogin) -> {
            try {
                final String email = emailField.getText().trim();
                final String password = new String(passwordField.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    status.setText("Missing email or password");
                    return;
                }

                // === Clean Architecture Interactor ===
                AuthService auth = new AuthService();
                String jwt = auth.login(email, password);

                if (jwt == null) {
                    status.setText("Invalid login.");
                    return;
                }

                sessionDAO.setJwtToken(jwt);
                success = true;
                dispose();

            }
            catch (
                Exception ex
            ) {
                System.err.println("LoginPage login error: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
                status.setText("Login failed.");
            }
        });

        panel.add(title);
        panel.add(emailField);
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(status);

        return panel;
    }

    // ===============================================================
    // SIGNUP PANEL
    // ===============================================================
    private JPanel buildSignupPanel() {
        final JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        final JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        final JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        final JPasswordField p1 = new JPasswordField();
        p1.setBorder(BorderFactory.createTitledBorder("Password"));

        final JLabel status = new JLabel("", SwingConstants.CENTER);
        status.setForeground(Color.RED);

        final JButton signupBtn = new JButton("Sign Up");

        signupBtn.addActionListener(presssignup -> {
            try {
                final String email = emailField.getText().trim();
                final String password = new String(p1.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    status.setText("Email and password are required.");
                    return;
                }

                // === Clean Architecture Interactor ===
                AuthService auth = new AuthService();
                String jwt = auth.signup(email, password);

                if (jwt == null) {
                    status.setText("Signup failed (email may already exist)");
                    return;
                }

                sessionDAO.setJwtToken(jwt);
                success = true;
                dispose();

            }
            catch (Exception ex) {
                System.err.println("LoginPage signup error: " + ex.getMessage());
                for (StackTraceElement ste : ex.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
                status.setText("Signup failed.");
            }
        });

        panel.add(title);
        panel.add(emailField);
        panel.add(p1);
        panel.add(signupBtn);
        panel.add(status);

        return panel;
    }

    // ===============================================================
    // SUCCESS FLAG
    // ===============================================================
    /**
     * Indicates whether the operation completed successfully.
     *
     * @return true if the operation succeeded; false otherwise
     */
    public boolean wasSuccessful() {
        return success;
    }
}
