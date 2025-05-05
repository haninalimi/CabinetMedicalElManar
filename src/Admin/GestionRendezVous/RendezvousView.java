package Admin.GestionRendezVous;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RendezvousView extends JPanel {
    private JTable rdvTable;
    private DefaultTableModel tableModel;
    private JTextField dateField, motifField, searchField;
    private JComboBox<String> patientCombo, statutCombo, heureCombo, minuteCombo;
    private JButton saveButton, cancelButton, refreshButton, searchButton;
    private JMenuItem editItem, deleteItem, deleteAllItem;


    public RendezvousView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titre
        add(createTitlePanel(), BorderLayout.NORTH);

        // Panel principal avec formulaire et recherche
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createSearchPanel(), BorderLayout.EAST);

        // Split pane pour formulaire et tableau
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(mainPanel);
        splitPane.setBottomComponent(createTablePanel());
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel title = new JLabel("GESTION DES RENDEZ-VOUS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 153));
        panel.add(title);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 10));
        TitledBorder searchBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(215, 213, 213, 255), 2), // Bordure plus épaisse (2 px)
                "Recherche", // Ton titre
                TitledBorder.LEFT, // Alignement du titre à gauche
                TitledBorder.TOP, // Position du titre en haut
                new Font("Segoe UI", Font.BOLD, 16) // Style du titre
        );
        searchPanel.setBorder(searchBorder);
        searchPanel.setPreferredSize(new Dimension(300, 200));

        // Champ de recherche
        JPanel searchInputPanel = new JPanel(new BorderLayout(5, 5));
        JLabel searchLabel = new JLabel("Rechercher par patient:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchInputPanel.add(searchLabel, BorderLayout.NORTH);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchInputPanel.add(searchField, BorderLayout.CENTER);

        // Boutons recherche et actualiser
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        searchButton = createButton("Rechercher", new Color(70, 130, 180));
        buttonsPanel.add(searchButton);

        refreshButton = createButton("Actualiser", new Color(70, 130, 180));
        buttonsPanel.add(refreshButton);

        searchInputPanel.add(buttonsPanel, BorderLayout.SOUTH);
        searchPanel.add(searchInputPanel, BorderLayout.NORTH);

        return searchPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        // Création d'un border épais avec titre centré
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(215, 213, 213, 255), 2), // Bordure plus épaisse (épaisseur 2)
                "Formulaire Rendez-vous",
                TitledBorder.LEFT, // Centrer le titre
                TitledBorder.TOP,    // Positionner le titre en haut
                new Font("Segoe UI", Font.BOLD, 16) // Police du titre
        );
        panel.setBorder(border);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuration des labels
        JLabel[] labels = {
                new JLabel("Patient:"), new JLabel("Date:"),
                new JLabel("Heure:"), new JLabel("Motif:"),
                new JLabel("Statut:")
        };
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        // Configuration des champs
        patientCombo = new JComboBox<>();
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        heureCombo = new JComboBox<>();
        minuteCombo = new JComboBox<>();
        motifField = new JTextField(20);
        statutCombo = new JComboBox<>(new String[]{"prévu", "annulé", "terminé"});

        // Ajout des composants
        int row = 0;
        addFormRow(panel, gbc, labels[0], patientCombo, row++);

        // Ligne pour la date et l'heure
        JPanel dateHeurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateHeurePanel.add(dateField);
        dateHeurePanel.add(new JLabel("à"));
        dateHeurePanel.add(heureCombo);
        dateHeurePanel.add(new JLabel(":"));
        dateHeurePanel.add(minuteCombo);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(labels[1], gbc);

        gbc.gridx = 1;
        panel.add(dateHeurePanel, gbc);
        row++;

        addFormRow(panel, gbc, labels[3], motifField, row++);
        addFormRow(panel, gbc, labels[4], statutCombo, row++);

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

        panel.add(buttonPanel, gbc);

        return panel;
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

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Liste des Rendez-vous"));

        String[] columns = {"ID", "Patient", "Date/Heure", "Motif", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rdvTable = new JTable(tableModel);
        rdvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rdvTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdvTable.setRowHeight(25);
        rdvTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        rdvTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPopupMenu popupMenu = new JPopupMenu();

        editItem = new JMenuItem("Modifier");
        popupMenu.add(editItem);

        deleteItem = new JMenuItem("Supprimer");
        popupMenu.add(deleteItem);

        deleteAllItem = new JMenuItem("Supprimer tous");
        popupMenu.add(deleteAllItem);

        rdvTable.setComponentPopupMenu(popupMenu);

        panel.add(new JScrollPane(rdvTable), BorderLayout.CENTER);
        return panel;
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

    public void initHeureMinuteCombos() {
        // Heures de 8h à 18h
        for (int i = 8; i <= 18; i++) {
            heureCombo.addItem(String.format("%02d", i));
        }

        // Minutes par pas de 15 (00, 15, 30, 45)
        minuteCombo.addItem("00");
        minuteCombo.addItem("15");
        minuteCombo.addItem("30");
        minuteCombo.addItem("45");

        // Par défaut, heure actuelle
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String[] timeParts = sdf.format(new Date()).split(":");
        heureCombo.setSelectedItem(timeParts[0]);
        minuteCombo.setSelectedItem(timeParts[1]);
    }

    public void loadPatients(List<String> patients) {
        patientCombo.removeAllItems();
        for (String patient : patients) {
            patientCombo.addItem(patient);
        }
    }

    public void loadRendezVous(List<Object[]> rdvs) {
        tableModel.setRowCount(0);
        for (Object[] row : rdvs) {
            tableModel.addRow(row);
        }
    }

    // Getters pour les composants
    public JTable getRdvTable() { return rdvTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getDateField() { return dateField; }
    public JTextField getMotifField() { return motifField; }
    public JTextField getSearchField() { return searchField; }
    public JComboBox<String> getPatientCombo() { return patientCombo; }
    public JComboBox<String> getStatutCombo() { return statutCombo; }
    public JComboBox<String> getHeureCombo() { return heureCombo; }
    public JComboBox<String> getMinuteCombo() { return minuteCombo; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getSearchButton() { return searchButton; }
    public JMenuItem getEditMenuItem() { return editItem; }
    public JMenuItem getDeleteMenuItem() { return deleteItem; }
    public JMenuItem getDeleteAllMenuItem() { return deleteAllItem; }
}