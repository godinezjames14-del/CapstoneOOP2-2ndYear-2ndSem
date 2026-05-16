package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class StudentApplicationsController {

    @FXML private Label lblTotalApplied;
    @FXML private Label lblPending;
    @FXML private Label lblApproved;
    @FXML private TableView<ApplicationRecord> tableApplications;
    @FXML private TableColumn<ApplicationRecord, String> colAppDate;
    @FXML private TableColumn<ApplicationRecord, String> colAppScholarship;
    @FXML private TableColumn<ApplicationRecord, String> colAppSponsor;
    @FXML private TableColumn<ApplicationRecord, String> colAppStatus;

    private int studentID;
    private final ObservableList<ApplicationRecord> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colAppDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().date()));
        colAppScholarship.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().scholarship()));
        colAppSponsor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sponsor()));
        colAppStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));

        // Color-code the Status column
        colAppStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "approved"  -> setStyle("-fx-text-fill: #155724; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        case "rejected"  -> setStyle("-fx-text-fill: #721c24; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        default          -> setStyle("-fx-text-fill: #856404; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
        loadApplications();
    }

    private void loadApplications() {
        data.clear();
        // scholarshipID column added in updated SQL — join scholarships to get name & sponsor
        String query =
                "SELECT a.appliedDate, s.name AS scholarship, s.sponsor, a.status " +
                        "FROM applications a " +
                        "JOIN scholarships s ON a.scholarshipID = s.scholarshipID " +
                        "WHERE a.studentID = ? " +
                        "ORDER BY a.appliedDate DESC";

        int total = 0, pending = 0, approved = 0;

        try (Connection conn = mySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String rawDate = rs.getString("appliedDate");
                String date = rawDate != null ? rawDate.substring(0, 16).replace("T", " ") : "N/A";
                String status = rs.getString("status");

                data.add(new ApplicationRecord(date, rs.getString("scholarship"), rs.getString("sponsor"), status));

                total++;
                if ("Pending".equalsIgnoreCase(status))  pending++;
                if ("Approved".equalsIgnoreCase(status)) approved++;
            }

            tableApplications.setItems(data);
            lblTotalApplied.setText(String.valueOf(total));
            lblPending.setText(String.valueOf(pending));
            lblApproved.setText(String.valueOf(approved));

        } catch (SQLException e) {
            System.err.println("Error loading student applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public record ApplicationRecord(String date, String scholarship, String sponsor, String status) {}
}
