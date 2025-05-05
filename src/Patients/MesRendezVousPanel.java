package Patients;

import client.RmiClient;
import interfaces.RendezVousService;
import utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MesRendezVousPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTable rendezVousTable;
    private DefaultTableModel tableModel;
    private int patientId;
    private String email;

    private JTextField dateRdvField;
    private JTextField motifField;
    private JComboBox<String> heureComboBox;

    public MesRendezVousPanel(String prenom, String nom, String email) {
        this.email = email;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        this.patientId = getPatientId(email);

        JPanel headerPanel = createHeaderPanel(prenom, nom);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.SOUTH);

        loadRendezVous();
    }

    private JPanel createHeaderPanel(String prenom, String nom) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Mes Rendez-vous", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.setBackground(Color.WHITE);

        // Modèle de table avec seulement 3 colonnes (ID, Date, Motif)
        String[] columnNames = {"ID", "Date", "Motif"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non éditables
            }
        };

        rendezVousTable = new JTable(tableModel);
        rendezVousTable.setRowHeight(35);
        rendezVousTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rendezVousTable.setIntercellSpacing(new Dimension(0, 0));
        rendezVousTable.setShowGrid(false);
        rendezVousTable.setFillsViewportHeight(true);

        // Centrer le texte dans les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < rendezVousTable.getColumnCount(); i++) {
            rendezVousTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style du header
        JTableHeader header = rendezVousTable.getTableHeader();
        header.setFont(LABEL_FONT.deriveFont(Font.BOLD));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scrollPane = new JScrollPane(rendezVousTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Menu contextuel au clic droit
        rendezVousTable.setComponentPopupMenu(createPopupMenu());
        rendezVousTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                int row = rendezVousTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    rendezVousTable.setRowSelectionInterval(row, row);
                    createPopupMenu().show(rendezVousTable, e.getX(), e.getY());
                }
            }
        });

        return tablePanel;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem annulerItem = new JMenuItem("Annuler le rendez-vous");
        annulerItem.addActionListener(e -> annulerRendezVous());
        popupMenu.add(annulerItem);
        return popupMenu;
    }

    private void annulerRendezVous() {
        int selectedRow = rendezVousTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rendez-vous à annuler", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rdvId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir annuler ce rendez-vous?",
                "Confirmation d'annulation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DatabaseConnection.getConnection()) {
                String query = "UPDATE rendezvous SET statut = 'annulé' WHERE id = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setInt(1, rdvId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Rendez-vous annulé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadRendezVous();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'annulation du rendez-vous", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setBackground(SECONDARY_COLOR);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        JLabel dateLabel = new JLabel("Date (AAAA-MM-JJ):");
        dateLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(dateLabel, gbc);

        dateRdvField = new JTextField(10);
        dateRdvField.setText(today);
        dateRdvField.setFont(LABEL_FONT);

        JLabel heureLabel = new JLabel("Heure:");
        heureLabel.setFont(LABEL_FONT);

        heureComboBox = new JComboBox<>();
        for (int i = 8; i <= 18; i++) {
            heureComboBox.addItem(String.format("%02d:00", i));
        }
        heureComboBox.setFont(LABEL_FONT);

        JPanel dateHeurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateHeurePanel.setBackground(SECONDARY_COLOR);
        dateHeurePanel.add(dateRdvField);
        dateHeurePanel.add(Box.createHorizontalStrut(10));
        dateHeurePanel.add(heureLabel);
        dateHeurePanel.add(heureComboBox);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(dateHeurePanel, gbc);

        JLabel motifLabel = new JLabel("Motif:");
        motifLabel.setFont(LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(motifLabel, gbc);

        motifField = new JTextField(20);
        motifField.setFont(LABEL_FONT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(motifField, gbc);

        JButton requestButton = new JButton("Demander un rendez-vous");
        requestButton.setFont(BUTTON_FONT);
        requestButton.setBackground(PRIMARY_COLOR);
        requestButton.setForeground(Color.WHITE);
        requestButton.setFocusPainted(false);
        requestButton.addActionListener(e -> requestRendezVous());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(requestButton, gbc);

        return formPanel;
    }

    private int getPatientId(String email) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT p.id FROM patients p JOIN users u ON p.user_id = u.id WHERE u.email = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadRendezVous() {
        try (Connection con = DatabaseConnection.getConnection()) {
            // Seulement les rendez-vous prévus (non annulés)
            String query = "SELECT * FROM rendezvous WHERE patient_id = ? AND statut = 'prévu'";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Vider la table

            while (rs.next()) {
                int rdvId = rs.getInt("id");
                String date = rs.getString("date_rdv");
                String motif = rs.getString("motif");

                tableModel.addRow(new Object[]{rdvId, date, motif});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void requestRendezVous() {
        String date = dateRdvField.getText().trim();
        String heure = (String) heureComboBox.getSelectedItem();
        String motif = motifField.getText().trim();

        if (date.isEmpty() || motif.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateHeure = date + " " + heure + ":00";

        RendezVousService service = RmiClient.getService();
        if (service != null) {
            try {
                String reponse = service.demanderRendezVous(patientId, dateHeure, motif);
                if ("acceptee".equalsIgnoreCase(reponse)) {
                    JOptionPane.showMessageDialog(this, " Rendez-vous accepté par le serveur !");
                    loadRendezVous();
                } else {
                    JOptionPane.showMessageDialog(this, " Demande refusée par le serveur.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Service RMI non disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}