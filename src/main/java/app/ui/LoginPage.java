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

import api.SupabaseAuthClient;
import dataaccess.EnvConfig;
import usecase.session.SessionDataAccessInterface;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:LineLength"})
public class LoginPage extends JDialog {

    @SuppressWarnings({"checkstyle:ExplicitInitialization", "checkstyle:SuppressWarnings"})
    private boolean success = false;
    @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
    private final SessionDataAccessInterface sessionDAO;

    // Main panels
    private JPanel mainPanel;
    private CardLayout cardLayout;

    @SuppressWarnings({"checkstyle:LambdaParameterName", "checkstyle:FinalLocalVariable", "checkstyle:MagicNumber", "checkstyle:TrailingComment", "checkstyle:RegexpSinglelineJava", "checkstyle:AbbreviationAsWordInName", "checkstyle:SuppressWarnings"})
    public LoginPage(JFrame parent, SessionDataAccessInterface sessionDAO) {
        super(parent, "Billionaire — Login / Signup", true); // true = modal
        this.sessionDAO = sessionDAO;

        setSize(420, 360);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // === Top Buttons ===
        JButton loginTab = new JButton("Login");
        JButton signupTab = new JButton("Sign Up");

        loginTab.setFocusable(false);
        signupTab.setFocusable(false);

        JPanel tabPanel = new JPanel(new GridLayout(1, 2));
        tabPanel.add(loginTab);
        tabPanel.add(signupTab);

        // === Card Layout (switches between login and signup panels) ===
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panels
        mainPanel.add(buildLoginPanel(), "login");
        mainPanel.add(buildSignupPanel(), "signup");

        // === Dialog Layout ===
        setLayout(new BorderLayout());
        add(tabPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Actions
        loginTab.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        signupTab.addActionListener(e -> cardLayout.show(mainPanel, "signup"));
    }

    // ===============================================================
    // LOGIN PANEL
    // ===============================================================
    @SuppressWarnings({"checkstyle:IllegalCatch", "checkstyle:RightCurly", "checkstyle:FinalLocalVariable", "checkstyle:ReturnCount", "checkstyle:LambdaBodyLength", "checkstyle:LambdaParameterName", "checkstyle:MagicNumber", "checkstyle:SuppressWarnings", "checkstyle:LineLength"})
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Log In", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        JLabel status = new JLabel("", SwingConstants.CENTER);
        status.setForeground(Color.RED);

        JButton loginBtn = new JButton("Log In");

        loginBtn.addActionListener((ActionEvent e) -> {
            try {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    status.setText("Missing email or password");
                    return;
                }

                SupabaseAuthClient auth = new SupabaseAuthClient(
                        EnvConfig.getSupabaseUrl(),
                        EnvConfig.getSupabaseAnonKey()
                );

                var result = auth.signIn(email, password);
                String jwt = result.optString("access_token", null);

                if (jwt == null) {
                    status.setText("Invalid login.");
                    return;
                }

                sessionDAO.setJwtToken(jwt);
                success = true;
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
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
    @SuppressWarnings({"checkstyle:RegexpMultiline", "checkstyle:IllegalCatch", "checkstyle:RightCurly", "checkstyle:EmptyLineSeparator", "checkstyle:FinalLocalVariable", "checkstyle:ReturnCount", "checkstyle:LambdaBodyLength", "checkstyle:LambdaParameterName", "checkstyle:MagicNumber", "checkstyle:SuppressWarnings", "checkstyle:LineLength"})
    private JPanel buildSignupPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JTextField emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField p1 = new JPasswordField();
        p1.setBorder(BorderFactory.createTitledBorder("Password"));

        JLabel status = new JLabel("", SwingConstants.CENTER);
        status.setForeground(Color.RED);

        JButton signupBtn = new JButton("Sign Up");

        signupBtn.addActionListener(e -> {
            try {
                String email = emailField.getText().trim();
                String password = new String(p1.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    status.setText("Email and password are required.");
                    return;
                }

                SupabaseAuthClient auth = new SupabaseAuthClient(
                        EnvConfig.getSupabaseUrl(),
                        EnvConfig.getSupabaseAnonKey()
                );

                var result = auth.signUp(email, password);
                String jwt = result.optString("access_token", null);


                if (jwt == null) {
                    status.setText("Signup failed (email may already exist)");
                    return;
                }

                sessionDAO.setJwtToken(jwt);
                success = true;
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
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
    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public boolean wasSuccessful() {
        return success;
    }
}
