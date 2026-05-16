package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * AdminDashboardController
 *
 * Provides admin-only capabilities:
 *  1. Overview stats (total funds, scholarships, users)
 *  2. Scholarship management (Add / Edit / Activate / Deactivate)
 *  3. Fund management — view all donations across every sponsor
 *  4. User management — view all users, change role or delete
 */
public class AdminDashboardController {

    // ── Overview stat labels ──────────────────────────────────────────────────
    @FXML private Label lblTotalFunds;
    @FXML private Label lblTotalScholarships;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblTotalSponsors;

    // ── Scholarship management ────────────────────────────────────────────────
    @FXML private TableView<ScholarshipRow> tableScholarships;
    @FXML private TableColumn<ScholarshipRow, Integer>  colSchID;
    @FXML private TableColumn<ScholarshipRow, String>   colSchName;
    @FXML private TableColumn<ScholarshipRow, String>   colSchSponsor;
    @FXML private TableColumn<ScholarshipRow, Double>   colSchGrade;
    @FXML private TableColumn<ScholarshipRow, String>   colSchLocation;
    @FXML private TableColumn<ScholarshipRow, String>   colSchStatus;
    @FXML private TableColumn<ScholarshipRow, Void>     colSchActions;

    // Scholarship form fields
    @FXML private TextField txtSchName;
    @FXML private TextField txtSchSponsor;
    @FXML private TextField txtSchGrade;
    @FXML private TextField txtSchLocation;
    @FXML private TextArea  txtSchDescription;
    @FXML private ComboBox<String> comboSchStatus;
    @FXML private Label     lblSchFormStatus;
    @FXML private Button    btnSaveScholarship;

    private int editingScholarshipID = -1; // -1 means "adding new"

    // ── Funds management ─────────────────────────────────────────────────────
    @FXML private TableView<DonationRow> tableFunds;
    @FXML private TableColumn<DonationRow, String>  colFundDate;
    @FXML private TableColumn<DonationRow, String>  colFundSponsor;
    @FXML private TableColumn<DonationRow, String>  colFundScholarship;
    @FXML private TableColumn<DonationRow, Double>  colFundAmount;
    @FXML private TableColumn<DonationRow, String>  colFundNote;
    @FXML private Label lblFundTotal;

    // ── User management ───────────────────────────────────────────────────────
    @FXML private TableView<UserRow>             tableUsers;
    @FXML private TableColumn<UserRow, Integer>  colUserID;
    @FXML private TableColumn<UserRow, String>   colUserName;
    @FXML private TableColumn<UserRow, String>   colUserEmail;
    @FXML private TableColumn<UserRow, String>   colUserRole;
    @FXML private TableColumn<UserRow, String>   colUserCreated;
    @FXML private TableColumn<UserRow, Void>     colUserActions;

    private final NumberFormat peso = NumberFormat.getInstance(new Locale("en", "PH"));

    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        peso.setMinimumFractionDigits(2);
        peso.setMaximumFractionDigits(2);

        comboSchStatus.getItems().addAll("Active", "Inactive");
        comboSchStatus.getSelectionModel().selectFirst();

        setupScholarshipTable();
        setupFundsTable();
        setupUsersTable();

        loadStats();
        loadScholarships();
        loadFunds();
        loadUsers();
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private void loadStats() {
        try (Connection conn = mySQLConnection.getConnection()) {

            // Total funds collected
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COALESCE(SUM(amount),0) AS total FROM donations")) {
                if (rs.next()) lblTotalFunds.setText("₱" + peso.format(rs.getDouble("total")));
            }

            // Total scholarships
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) AS cnt FROM scholarships")) {
                if (rs.next()) lblTotalScholarships.setText(String.valueOf(rs.getInt("cnt")));
            }

            // Students
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM users WHERE role = 'STUDENT'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblTotalStudents.setText(String.valueOf(rs.getInt("cnt")));
            }

            // Sponsors
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt FROM users WHERE role = 'SPONSOR'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblTotalSponsors.setText(String.valueOf(rs.getInt("cnt")));
            }

        } catch (SQLException e) {
            System.err.println("Admin loadStats error: " + e.getMessage());
        }
    }

    // ── Scholarship Table ─────────────────────────────────────────────────────

    private void setupScholarshipTable() {
        colSchID.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()).asObject());
        colSchName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        colSchSponsor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sponsor()));
        colSchGrade.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().gradeRequired()).asObject());
        colSchLocation.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().location()));
        colSchStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));

        // Status cell color
        colSchStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Active".equals(item)
                        ? "-fx-text-fill: #28a745; -fx-font-weight: bold;"
                        : "-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            }
        });

        // Actions column: Edit + Toggle Status
        colSchActions.setCellFactory(makeScholarshipActionCells());
    }

    private Callback<TableColumn<ScholarshipRow, Void>, TableCell<ScholarshipRow, Void>> makeScholarshipActionCells() {
        return param -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏ Edit");
            private final Button btnToggle = new Button();

            {
                btnEdit.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                btnToggle.setStyle("-fx-background-radius: 6; -fx-cursor: hand;");

                btnEdit.setOnAction(e -> {
                    ScholarshipRow row = getTableView().getItems().get(getIndex());
                    populateScholarshipForm(row);
                });

                btnToggle.setOnAction(e -> {
                    ScholarshipRow row = getTableView().getItems().get(getIndex());
                    toggleScholarshipStatus(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                ScholarshipRow row = getTableView().getItems().get(getIndex());
                if ("Active".equals(row.status())) {
                    btnToggle.setText("⛔ Deactivate");
                    btnToggle.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    btnToggle.setText("✅ Activate");
                    btnToggle.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                }
                javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnEdit, btnToggle);
                setGraphic(box);
            }
        };
    }

    private void loadScholarships() {
        ObservableList<ScholarshipRow> list = FXCollections.observableArrayList();
        String sql = "SELECT scholarshipID, name, sponsor, gradeRequired, location, status FROM scholarships ORDER BY scholarshipID";
        try (Connection conn = mySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ScholarshipRow(
                        rs.getInt("scholarshipID"),
                        rs.getString("name"),
                        rs.getString("sponsor"),
                        rs.getDouble("gradeRequired"),
                        rs.getString("location"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("loadScholarships error: " + e.getMessage());
        }
        tableScholarships.setItems(list);
    }

    private void populateScholarshipForm(ScholarshipRow row) {
        editingScholarshipID = row.id();
        txtSchName.setText(row.name());
        txtSchSponsor.setText(row.sponsor());
        txtSchGrade.setText(String.valueOf(row.gradeRequired()));
        txtSchLocation.setText(row.location());
        comboSchStatus.setValue(row.status());

        // Load description separately
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT description FROM scholarships WHERE scholarshipID = ?")) {
            ps.setInt(1, row.id());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtSchDescription.setText(rs.getString("description"));
        } catch (SQLException e) { e.printStackTrace(); }

        btnSaveScholarship.setText("💾 Update Scholarship");
        showSchStatus("Editing: " + row.name(), "#17a2b8");
    }

    @FXML
    private void handleClearScholarshipForm(MouseEvent event) {
        editingScholarshipID = -1;
        txtSchName.clear();
        txtSchSponsor.clear();
        txtSchGrade.clear();
        txtSchLocation.clear();
        txtSchDescription.clear();
        comboSchStatus.getSelectionModel().selectFirst();
        btnSaveScholarship.setText("➕ Add Scholarship");
        lblSchFormStatus.setText("");
    }

    @FXML
    private void handleSaveScholarship(MouseEvent event) {
        String name   = txtSchName.getText().trim();
        String sponsor = txtSchSponsor.getText().trim();
        String gradeStr = txtSchGrade.getText().trim();
        String location = txtSchLocation.getText().trim();
        String desc   = txtSchDescription.getText().trim();
        String status = comboSchStatus.getValue();

        if (name.isEmpty() || sponsor.isEmpty() || gradeStr.isEmpty() || location.isEmpty()) {
            showSchStatus("❌ Please fill in all required fields.", "#dc3545");
            return;
        }

        double grade;
        try {
            grade = Double.parseDouble(gradeStr);
            if (grade < 0 || grade > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showSchStatus("❌ Grade must be a number between 0 and 100.", "#dc3545");
            return;
        }

        try (Connection conn = mySQLConnection.getConnection()) {
            if (editingScholarshipID == -1) {
                // INSERT
                String sql = "INSERT INTO scholarships (name, sponsor, gradeRequired, location, description, status) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, sponsor);
                    ps.setDouble(3, grade);
                    ps.setString(4, location);
                    ps.setString(5, desc.isEmpty() ? null : desc);
                    ps.setString(6, status);
                    ps.executeUpdate();
                }
                showSchStatus("✅ Scholarship \"" + name + "\" added successfully!", "#155724");
            } else {
                // UPDATE
                String sql = "UPDATE scholarships SET name=?, sponsor=?, gradeRequired=?, location=?, description=?, status=? WHERE scholarshipID=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, sponsor);
                    ps.setDouble(3, grade);
                    ps.setString(4, location);
                    ps.setString(5, desc.isEmpty() ? null : desc);
                    ps.setString(6, status);
                    ps.setInt(7, editingScholarshipID);
                    ps.executeUpdate();
                }
                showSchStatus("✅ Scholarship updated successfully!", "#155724");
                editingScholarshipID = -1;
                btnSaveScholarship.setText("➕ Add Scholarship");
            }
            loadScholarships();
            loadStats();
        } catch (SQLException e) {
            showSchStatus("❌ Database error: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }

    private void toggleScholarshipStatus(ScholarshipRow row) {
        String newStatus = "Active".equals(row.status()) ? "Inactive" : "Active";
        String sql = "UPDATE scholarships SET status = ? WHERE scholarshipID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, row.id());
            ps.executeUpdate();
            loadScholarships();
            loadStats();
        } catch (SQLException e) {
            System.err.println("toggleScholarshipStatus error: " + e.getMessage());
        }
    }

    private void showSchStatus(String msg, String color) {
        lblSchFormStatus.setStyle("-fx-text-fill: " + color + ";");
        lblSchFormStatus.setText(msg);
    }

    // ── Funds Table ───────────────────────────────────────────────────────────

    private void setupFundsTable() {
        colFundDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().date()));
        colFundSponsor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sponsorName()));
        colFundScholarship.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().scholarshipName()));
        colFundAmount.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().amount()).asObject());

        // Format amount column as peso
        colFundAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "₱" + peso.format(item));
            }
        });

        colFundNote.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().note()));
    }

    private void loadFunds() {
        ObservableList<DonationRow> list = FXCollections.observableArrayList();
        String sql =
                "SELECT d.donationDate, u.name AS sponsorName, s.name AS scholarshipName, d.amount, d.note " +
                        "FROM donations d " +
                        "JOIN users u ON d.sponsorID = u.userID " +
                        "JOIN scholarships s ON d.scholarshipID = s.scholarshipID " +
                        "ORDER BY d.donationDate DESC";

        double total = 0;
        try (Connection conn = mySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                double amt = rs.getDouble("amount");
                total += amt;
                String dateRaw = rs.getString("donationDate");
                String date = (dateRaw != null && dateRaw.length() >= 16)
                        ? dateRaw.substring(0, 16).replace("T", " ") : (dateRaw != null ? dateRaw : "N/A");
                list.add(new DonationRow(
                        date,
                        rs.getString("sponsorName"),
                        rs.getString("scholarshipName"),
                        amt,
                        rs.getString("note") != null ? rs.getString("note") : "—"
                ));
            }
        } catch (SQLException e) {
            System.err.println("loadFunds error: " + e.getMessage());
        }
        tableFunds.setItems(list);
        lblFundTotal.setText("Total Collected: ₱" + peso.format(total));
    }

    @FXML
    private void handleRefreshFunds(MouseEvent event) {
        loadFunds();
        loadStats();
    }

    // ── User Table ────────────────────────────────────────────────────────────

    private void setupUsersTable() {
        colUserID.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().id()).asObject());
        colUserName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        colUserEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().email()));
        colUserRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().role()));
        colUserCreated.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().createdAt()));

        // Role cell color
        colUserRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "ADMIN"   -> "-fx-text-fill: #800000; -fx-font-weight: bold;";
                    case "SPONSOR" -> "-fx-text-fill: #856404; -fx-font-weight: bold;";
                    default        -> "-fx-text-fill: #155724; -fx-font-weight: bold;";
                };
                setStyle(color);
            }
        });

        colUserActions.setCellFactory(makeUserActionCells());
    }

    private Callback<TableColumn<UserRow, Void>, TableCell<UserRow, Void>> makeUserActionCells() {
        return param -> new TableCell<>() {
            private final ComboBox<String> roleCombo = new ComboBox<>();
            private final Button btnApply  = new Button("Apply");
            private final Button btnDelete = new Button("🗑 Delete");

            {
                roleCombo.getItems().addAll("STUDENT", "SPONSOR", "ADMIN");
                roleCombo.setStyle("-fx-background-radius: 6;");

                btnApply.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

                btnApply.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    changeUserRole(row.id(), roleCombo.getValue(), row.name());
                });

                btnDelete.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    confirmDeleteUser(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                UserRow row = getTableView().getItems().get(getIndex());
                roleCombo.setValue(row.role());
                javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, roleCombo, btnApply, btnDelete);
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                setGraphic(box);
            }
        };
    }

    private void loadUsers() {
        ObservableList<UserRow> list = FXCollections.observableArrayList();
        String sql = "SELECT userID, name, email, role, created_at FROM users ORDER BY userID";
        try (Connection conn = mySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new UserRow(
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("created_at") != null ? rs.getString("created_at").substring(0, 10) : "N/A"
                ));
            }
        } catch (SQLException e) {
            System.err.println("loadUsers error: " + e.getMessage());
        }
        tableUsers.setItems(list);
    }

    private void changeUserRole(int userID, String newRole, String userName) {
        String sql = "UPDATE users SET role = ? WHERE userID = ?";
        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, userID);
            ps.executeUpdate();
            loadUsers();
            loadStats();
            showAlert(Alert.AlertType.INFORMATION, "Role Updated",
                    userName + " has been set to " + newRole + ".");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update role: " + e.getMessage());
        }
    }

    private void confirmDeleteUser(UserRow row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete \"" + row.name() + "\"?");
        confirm.setContentText("This action cannot be undone. Donations linked to this user will also be removed.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) deleteUser(row.id());
        });
    }

    private void deleteUser(int userID) {
        try (Connection conn = mySQLConnection.getConnection()) {
            // Remove donations first to avoid FK violation
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM donations WHERE sponsorID = ?")) {
                ps.setInt(1, userID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE userID = ?")) {
                ps.setInt(1, userID);
                ps.executeUpdate();
            }
            loadUsers();
            loadFunds();
            loadStats();
            showAlert(Alert.AlertType.INFORMATION, "User Deleted", "User has been removed successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not delete user: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshUsers(MouseEvent event) {
        loadUsers();
        loadStats();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    // ── Inner records ─────────────────────────────────────────────────────────

    public record ScholarshipRow(int id, String name, String sponsor,
                                 double gradeRequired, String location, String status) {}

    public record DonationRow(String date, String sponsorName,
                              String scholarshipName, double amount, String note) {}

    public record UserRow(int id, String name, String email, String role, String createdAt) {}
}