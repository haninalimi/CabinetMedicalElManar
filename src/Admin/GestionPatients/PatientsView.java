package Admin.GestionPatients;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PatientsView extends JPanel {
    private JTable patientsTable;
    private DefaultTableModel tableModel;
    private JTextField nomField, prenomField, emailField, adresseField, telephoneField, dateNaissanceField, searchField;
    private JPasswordField passwordField;
    private JButton saveButton, cancelButton, refreshButton;
    private int currentPatientId = -1;

    public PatientsView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Titre
        add(createTitlePanel(), BorderLayout.NORTH);

        // 2. Panel principal (formulaire + recherche)
        add(createMainContentPanel(), BorderLayout.CENTER);

        // 3. Tableau avec scroll
        add(createTablePanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel title = new JLabel("GESTION DES PATIENTS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 153));
        titlePanel.add(title);
        return titlePanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createSearchPanel(), BorderLayout.EAST);
        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        TitledBorder formBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(215, 213, 213, 255), 2), // Bordure plus épaisse (2 px)
                "Formulaire Patient", // Titre adapté
                TitledBorder.LEFT, // Titre aligné à gauche
                TitledBorder.TOP, // Positionné en haut
                new Font("Segoe UI", Font.BOLD, 16) // Police du titre
        );
        formPanel.setBorder(formBorder);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels
        JLabel[] labels = {
                new JLabel("Nom:"), new JLabel("Prénom:"), new JLabel("Email:"),
                new JLabel("Mot de passe:"), new JLabel("Adresse:"),
                new JLabel("Téléphone:"), new JLabel("Date Naissance:")
        };
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        // Champs de texte
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        adresseField = new JTextField(20);
        telephoneField = new JTextField(20);
        dateNaissanceField = new JTextField(20);

        JTextField[] fields = {nomField, prenomField, emailField, adresseField, telephoneField, dateNaissanceField};
        for (JTextField field : fields) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            field.setPreferredSize(new Dimension(200, 25));
        }

        // Ajout des composants
        int row = 0;
        addFormRow(formPanel, gbc, labels[0], nomField, row++);
        addFormRow(formPanel, gbc, labels[1], prenomField, row++);
        addFormRow(formPanel, gbc, labels[2], emailField, row++);
        addFormRow(formPanel, gbc, labels[3], passwordField, row++);
        addFormRow(formPanel, gbc, labels[4], adresseField, row++);
        addFormRow(formPanel, gbc, labels[5], telephoneField, row++);
        addFormRow(formPanel, gbc, labels[6], dateNaissanceField, row++);

        // Boutons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        saveButton = createButton("Enregistrer", Color.GREEN.darker());
        buttonPanel.add(saveButton);

        cancelButton = createButton("Annuler", Color.RED.darker());
        buttonPanel.add(cancelButton);

        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 10));
        TitledBorder searchBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(215, 213, 213, 255), 2), // Bordure grise plus épaisse (2 px)
                "Recherche", // Titre à afficher
                TitledBorder.LEFT, // Titre aligné à gauche
                TitledBorder.TOP, // Position du titre en haut
                new Font("Segoe UI", Font.BOLD, 16) // Police du titre
        );
        searchPanel.setBorder(searchBorder);
        searchPanel.setPreferredSize(new Dimension(300, 200));

        JPanel searchMainPanel = new JPanel();
        searchMainPanel.setLayout(new BoxLayout(searchMainPanel, BoxLayout.Y_AXIS));

        // Champ de recherche
        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel searchLabel = new JLabel("Rechercher par email:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchInputPanel.add(searchLabel, BorderLayout.NORTH);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchInputPanel.add(searchField, BorderLayout.CENTER);

        // Boutons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1, 5, 5));

        JButton searchButton = createButton("Rechercher", new Color(70, 130, 180));
        searchButton.addActionListener(e -> {
            if (searchListener != null) searchListener.actionPerformed(e);
        });
        buttonsPanel.add(searchButton);

        refreshButton = createButton("Actualiser", new Color(70, 130, 180));
        buttonsPanel.add(refreshButton);

        searchMainPanel.add(searchInputPanel);
        searchMainPanel.add(Box.createVerticalStrut(10));
        searchMainPanel.add(buttonsPanel);

        searchPanel.add(searchMainPanel, BorderLayout.NORTH);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Liste des Patients"));

        // Modèle de tableau
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Adresse", "Téléphone", "Date Naissance"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientsTable = new JTable(tableModel);
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        patientsTable.setRowHeight(25);
        patientsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // menu de liste modifier supprimer et supprimer tous
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.addActionListener(e -> {
            if (editListener != null) editListener.actionPerformed(e);
        });
        contextMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.addActionListener(e -> {
            if (deleteListener != null) deleteListener.actionPerformed(e);
        });
        contextMenu.add(deleteItem);

        JMenuItem deleteAllItem = new JMenuItem("Supprimer tous");
        deleteAllItem.addActionListener(e -> {
            if (deleteAllListener != null) deleteAllListener.actionPerformed(e);
        });
        contextMenu.add(deleteAllItem);

        patientsTable.setComponentPopupMenu(contextMenu);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    // Getters
    public JTable getPatientsTable() { return patientsTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getNomField() { return nomField; }
    public JTextField getPrenomField() { return prenomField; }
    public JTextField getEmailField() { return emailField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JTextField getAdresseField() { return adresseField; }
    public JTextField getTelephoneField() { return telephoneField; }
    public JTextField getDateNaissanceField() { return dateNaissanceField; }
    public JTextField getSearchField() { return searchField; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public int getCurrentPatientId() { return currentPatientId; }
    public void setCurrentPatientId(int id) { currentPatientId = id; }

    // Listeners
    private ActionListener editListener;
    private ActionListener deleteListener;
    private ActionListener deleteAllListener;
    private ActionListener searchListener;

    public void setEditListener(ActionListener listener) { this.editListener = listener; }
    public void setDeleteListener(ActionListener listener) { this.deleteListener = listener; }
    public void setDeleteAllListener(ActionListener listener) { this.deleteAllListener = listener; }
    public void setSearchListener(ActionListener listener) { this.searchListener = listener; }

    public void annulerModification() {
        currentPatientId = -1;
        viderChamps();
        saveButton.setText("Enregistrer");
    }

    public void actualiserPage() {
        annulerModification();
        searchField.setText("");
    }

    void viderChamps() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        adresseField.setText("");
        telephoneField.setText("");
        dateNaissanceField.setText("");
    }
}