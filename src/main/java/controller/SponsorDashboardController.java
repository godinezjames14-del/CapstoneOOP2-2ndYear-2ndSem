package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class SponsorDashboardController {

    @FXML private Label lblTotalDonated;
    @FXML private Label lblScholarshipsSupported;
    @FXML private Label lblDonationsCount;
    @FXML private ComboBox<ScholarshipItem> comboScholarship;
    @FXML private TextField txtAmount;
    @FXML private TextArea txtNote;
    @FXML private Label lblDonateStatus;
    @FXML private TableView<DonationRecord> tableDonations;
    @FXML private TableColumn<DonationRecord, String> colDonDate;
    @FXML private TableColumn<DonationRecord, String> colDonScholarship;
    @FXML private TableColumn<DonationRecord, String> colDonAmount;
    @FXML private TableColumn<DonationRecord, String> colDonNote;

    // Quick amount buttons
    @FXML private Button btn1000;
    @FXML private Button btn2500;
    @FXML private Button btn5000;
    @FXML private Button btn10000;
    @FXML private Button btn25000;

    private int sponsorUserID;
    private final ObservableList<DonationRecord> donationData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadScholarships();
    }

    public void setSponsorUserID(int userID) {
        this.sponsorUserID = userID;
        loadDonationHistory();
        loadStats();
    }

    // ─── Quick amount selection ───────────────────────────────────────────────
    @FXML
    private void setQuickAmount(MouseEvent event) {
        Button clicked = (Button) event.getSource();
        String raw = clicked.getUserData().toString();
        // Format cleanly without trailing zeros
        double val = Double.parseDouble(raw);
        txtAmount.setText(val % 1 == 0 ? String.valueOf((long) val) : String.valueOf(val));

        // Visual feedback: reset all, highlight clicked
        for (Button b : new Button[]{btn1000, btn2500, btn5000, btn10000, btn25000}) {
            b.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8; -fx-cursor: hand;");
        }
        clicked.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
    }

    // ─── Handle Donate button ─────────────────────────────────────────────────
    @FXML
    private void handleDonate(MouseEvent event) {
        lblDonateStatus.setText("");

        ScholarshipItem selected = comboScholarship.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("❌ Please select a scholarship.", "#c0392b");
            return;
        }

        String amountText = txtAmount.getText().trim();
        if (amountText.isEmpty()) {
            showStatus("❌ Please enter a donation amount.", "#c0392b");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showStatus("❌ Please enter a valid positive number.", "#c0392b");
            return;
        }

        String note = txtNote.getText().trim();
        String insertSQL = "INSERT INTO donations (sponsorID, scholarshipID, amount, note) VALUES (?, ?, ?, ?)";

        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            ps.setInt(1, sponsorUserID);
            ps.setInt(2, selected.getId());
            ps.setDouble(3, amount);
            ps.setString(4, note.isEmpty() ? null : note);
            ps.executeUpdate();

            NumberFormat fmt = NumberFormat.getInstance(new Locale("en", "PH"));
            fmt.setMinimumFractionDigits(2);
            fmt.setMaximumFractionDigits(2);
            showStatus("✅ Donation of ₱" + fmt.format(amount) + " to \"" + selected.getName() + "\" was successful!", "#155724");

            // Reset form
            comboScholarship.getSelectionModel().clearSelection();
            txtAmount.clear();
            txtNote.clear();
            resetQuickButtons();

            // Refresh data
            loadDonationHistory();
            loadStats();

        } catch (SQLException e) {
            System.err.println("Donation insert error: " + e.getMessage());
            showStatus("❌ Database error. Please try again.", "#c0392b");
            e.printStackTrace();
        }
    }

    // ─── Load active scholarships into combobox ───────────────────────────────
    private void loadScholarships() {
        comboScholarship.getItems().clear();
        String query = "SELECT scholarshipID, name FROM scholarships WHERE status = 'Active' ORDER BY name";

        try (Connection conn = mySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                comboScholarship.getItems().add(
                        new ScholarshipItem(rs.getInt("scholarshipID"), rs.getString("name"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Error loading scholarships for sponsor dashboard: " + e.getMessage());
        }
    }

    // ─── Load donation history for this sponsor ───────────────────────────────
    private void loadDonationHistory() {
        donationData.clear();
        String query =
                "SELECT d.donationDate, s.name AS scholarship, d.amount, d.note " +
                        "FROM donations d " +
                        "JOIN scholarships s ON d.scholarshipID = s.scholarshipID " +
                        "WHERE d.sponsorID = ? " +
                        "ORDER BY d.donationDate DESC";

        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, sponsorUserID);
            ResultSet rs = ps.executeQuery();

            NumberFormat fmt = NumberFormat.getInstance(new Locale("en", "PH"));
            fmt.setMinimumFractionDigits(2);
            fmt.setMaximumFractionDigits(2);

            while (rs.next()) {
                donationData.add(new DonationRecord(
                        rs.getString("donationDate") != null ? rs.getString("donationDate").substring(0, 16).replace("T", " ") : "N/A",
                        rs.getString("scholarship"),
                        "₱" + fmt.format(rs.getDouble("amount")),
                        rs.getString("note") != null ? rs.getString("note") : "—"
                ));
            }
            tableDonations.setItems(donationData);

        } catch (SQLException e) {
            System.err.println("Error loading donation history: " + e.getMessage());
        }
    }

    // ─── Load stats labels ────────────────────────────────────────────────────
    private void loadStats() {
        String query =
                "SELECT COUNT(*) AS total_count, " +
                        "       SUM(amount) AS total_amount, " +
                        "       COUNT(DISTINCT scholarshipID) AS unique_scholarships " +
                        "FROM donations WHERE sponsorID = ?";

        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, sponsorUserID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("total_count");
                double total = rs.getDouble("total_amount");
                int unique = rs.getInt("unique_scholarships");

                NumberFormat fmt = NumberFormat.getInstance(new Locale("en", "PH"));
                fmt.setMinimumFractionDigits(2);
                fmt.setMaximumFractionDigits(2);

                lblTotalDonated.setText("₱" + (count == 0 ? "0.00" : fmt.format(total)));
                lblDonationsCount.setText(String.valueOf(count));
                lblScholarshipsSupported.setText(String.valueOf(unique));
            }

        } catch (SQLException e) {
            System.err.println("Error loading sponsor stats: " + e.getMessage());
        }
    }

    // ─── Table column setup ───────────────────────────────────────────────────
    private void setupTableColumns() {
        colDonDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().date()));
        colDonScholarship.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().scholarship()));
        colDonAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().amount()));
        colDonNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().note()));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private void showStatus(String message, String color) {
        lblDonateStatus.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13;");
        lblDonateStatus.setText(message);
    }

    private void resetQuickButtons() {
        for (Button b : new Button[]{btn1000, btn2500, btn5000, btn10000, btn25000}) {
            b.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8; -fx-cursor: hand;");
        }
    }

    // ─── Inner helper records/classes ─────────────────────────────────────────
    public static class ScholarshipItem {
        private final int id;
        private final String name;

        public ScholarshipItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }

    public record DonationRecord(String date, String scholarship, String amount, String note) {}
}
