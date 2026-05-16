package main.java.controller;

import JDBC.mySQLConnection;
import javafx.application.Platform;
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
//    @FXML private TableView<ApplicationRecord> tableApplications;
//    @FXML private TableColumn<ApplicationRecord, String> colAppDate;
//    @FXML private TableColumn<ApplicationRecord, String> colAppScholarship;
//    @FXML private TableColumn<ApplicationRecord, String> colAppSponsor;
//    @FXML private TableColumn<ApplicationRecord, String> colAppStatus;

    private int studentID;
//    private final ObservableList<ApplicationRecord> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
//        colAppDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().date()));
//        colAppScholarship.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().scholarship()));
//        colAppSponsor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().sponsor()));
//        colAppStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status()));

        // Color-code the Status column dynamically
//        colAppStatus.setCellFactory(col -> new TableCell<>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    setText(item);
//                    switch (item.toLowerCase()) {
//                        case "approved"  -> setStyle("-fx-text-fill: #28A745; -fx-font-weight: bold; -fx-alignment: CENTER;");
//                        case "rejected"  -> setStyle("-fx-text-fill: #DC3545; -fx-font-weight: bold; -fx-alignment: CENTER;");
//                        default          -> setStyle("-fx-text-fill: #FFC107; -fx-font-weight: bold; -fx-alignment: CENTER;");
//                    }
//                }
//            }
//        });
    }

    public void setStudentID(int studentID) {
//        this.studentID = studentID;
//        loadApplications();
    }

//    private void loadApplications() {
//        data.clear();

        // Asynchronous database task thread to prevent dashboard UI stutters
//        new Thread(() -> {
//            String query = "SELECT a.appliedDate, s.name AS scholarship, s.sponsor, a.status " +
//                    "FROM applications a " +
//                    "JOIN scholarships s ON a.scholarshipID = s.scholarshipID " +
//                    "WHERE a.studentID = ? " +
//                    "ORDER BY a.appliedDate DESC";
//
//            ObservableList<ApplicationRecord> tempLiveList = FXCollections.observableArrayList();
//            int total = 0;
//            int pending = 0;
//            int approved = 0;
//
//            try (Connection conn = mySQLConnection.getConnection();
//                 PreparedStatement ps = conn.prepareStatement(query)) {
//
//                ps.setInt(1, studentID);
//                try (ResultSet rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        String rawDate = rs.getString("appliedDate");
//                        String date = rawDate != null && rawDate.length() >= 16
//                                ? rawDate.substring(0, 16).replace("T", " ")
//                                : (rawDate != null ? rawDate : "N/A");
//                        String status = rs.getString("status");
//                        String scholarship = rs.getString("scholarship");
//                        String sponsor = rs.getString("sponsor");
//
//                        tempLiveList.add(new ApplicationRecord(date, scholarship, sponsor, status));
//
//                        total++;
//                        if ("Pending".equalsIgnoreCase(status))  pending++;
//                        if ("Approved".equalsIgnoreCase(status)) approved++;
//                    }
//                }
//
//                // Push clean variables to variables before lambda passage context
//                final int finalTotal = total;
//                final int finalPending = pending;
//                final int finalApproved = approved;
//
//                // Sync operations back safely on the primary layout FX Thread
//                Platform.runLater(() -> {
//                    data.addAll(tempLiveList);
//                    tableApplications.setItems(data);
//
//                    if (lblTotalApplied != null) lblTotalApplied.setText(String.valueOf(finalTotal));
//                    if (lblPending != null) lblPending.setText(String.valueOf(finalPending));
//                    if (lblApproved != null) lblApproved.setText(String.valueOf(finalApproved));
//                });

//            } catch (SQLException e) {
//                System.err.println("❌ Error loading student applications: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }

//    public record ApplicationRecord(String date, String scholarship, String sponsor, String status) {}
}