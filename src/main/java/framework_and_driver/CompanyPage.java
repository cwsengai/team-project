package framework_and_driver;

import interface_adapter.controller.CompanyController;
import interface_adapter.view_model.CompanyViewModel;

import javax.swing.*;
import java.awt.*;

public class CompanyPage extends JFrame {

    private final CompanyViewModel viewModel;
    private CompanyController controller;

    private JTextField symbolField;
    private JLabel nameLabel;
    private JLabel sectorLabel;
    private JLabel industryLabel;
    private JTextArea descriptionArea;
    private JLabel errorLabel;

    public CompanyPage(CompanyViewModel viewModel,
                            CompanyController controller) {

        this.viewModel = viewModel;
        this.controller = controller;

        setTitle("Company Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        add(buildInputPanel(), BorderLayout.NORTH);
        add(buildOutputPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    public void setController(CompanyController controller) {
        this.controller = controller;
    }


    private JPanel buildInputPanel() {
        JPanel panel = new JPanel();

        symbolField = new JTextField(10);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e ->
                controller.onCompanySelected(symbolField.getText().trim())
        );

        panel.add(new JLabel("Symbol:"));
        panel.add(symbolField);
        panel.add(searchButton);

        return panel;
    }

    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("Name: ");
        sectorLabel = new JLabel("Sector: ");
        industryLabel = new JLabel("Industry: ");

        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        panel.add(nameLabel);
        panel.add(sectorLabel);
        panel.add(industryLabel);
        panel.add(new JScrollPane(descriptionArea));
        panel.add(errorLabel);

        return panel;
    }

    // Called by the presenter after it updates the ViewModel
    public void refresh() {
        if (viewModel.error != null) {
            errorLabel.setText(viewModel.error);
        } else {
            errorLabel.setText("");

            nameLabel.setText("Name: " + viewModel.name);
            sectorLabel.setText("Sector: " + viewModel.sector);
            industryLabel.setText("Industry: " + viewModel.industry);
            descriptionArea.setText(viewModel.description);
        }
    }
}
