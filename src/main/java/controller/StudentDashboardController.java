package main.java.controller;

import JDBC.mySQLConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML private Label labelScholar, labelSponsor;
    @FXML private Label lblTotalApplications;
    @FXML private Label lblPendingApplications;
    @FXML private Label lblApprovedApplications;

    private int currentUserID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGlobalPlatformStats();
    }

    // Call this method from your login script or main dashboard routing system to set the user
    public void setUserID(int userID) {
        this.currentUserID = userID;
        // Load personal student statistics as soon as the ID is safely received
        loadPersonalApplicationStats();
    }

    private void loadGlobalPlatformStats() {
        new Thread(() -> {
            String scholarshipCountQuery = "SELECT COUNT(*) AS total_scholarships FROM scholarships";
            String sponsorCountQuery = "SELECT COUNT(*) AS total_sponsors FROM users WHERE role='SPONSOR'";

            int totalScholarships = 0;
            int totalSponsors = 0;

            try (Connection conn = mySQLConnection.getConnection()) {

                // 1. Fetch total active scholarships
                try (PreparedStatement pstmt1 = conn.prepareStatement(scholarshipCountQuery);
                     ResultSet rs1 = pstmt1.executeQuery()) {
                    if (rs1.next()) {
                        totalScholarships = rs1.getInt("total_scholarships");
                    }
                }

                // 2. Fetch total sponsor users
                try (PreparedStatement pstmt2 = conn.prepareStatement(sponsorCountQuery);
                     ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        totalSponsors = rs2.getInt("total_sponsors");
                    }
                }

                final int finalScholarships = totalScholarships;
                final int finalSponsors = totalSponsors;

                // Safely update general count cards on the main FX thread
                Platform.runLater(() -> {
                    labelScholar.setText(String.valueOf(finalScholarships));
                    labelSponsor.setText(String.valueOf(finalSponsors));
                });

            } catch (SQLException e) {
                System.err.println("❌ Error fetching home dashboard summary stats from database:");
                e.printStackTrace();
            }
        }).start();
    }

    private void loadPersonalApplicationStats() {
        new Thread(() -> {
            String query = "SELECT " +
                    "COUNT(*) AS total, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending, " +
                    "SUM(CASE WHEN status = 'Approved' THEN 1 ELSE 0 END) AS approved " +
                    "FROM applications WHERE studentID = ?";

            try (Connection conn = mySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, currentUserID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final int total = rs.getInt("total");
                        final int pending = rs.getInt("pending");
                        final int approved = rs.getInt("approved");

                        // CRITICAL: UI changes MUST be wrapped inside Platform.runLater()
                        Platform.runLater(() -> {
                            lblTotalApplications.setText(String.valueOf(total));
                            lblPendingApplications.setText(String.valueOf(pending));
                            lblApprovedApplications.setText(String.valueOf(approved));
                        });
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Error loading student application stats:");
                e.printStackTrace();
            }
        }).start();
    }
}