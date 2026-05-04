package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.Scholarship;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ScholarshipListController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboLocation;
    @FXML private Slider sliderGrade;
    @FXML private Label lblGradeValue;
    @FXML private TableView<Scholarship> tableScholarships;
    @FXML private TableColumn<Scholarship, String> colName, colSponsor, colLocation;
    @FXML private TableColumn<Scholarship, Double> colGrade;

    private ObservableList<Scholarship> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Fetch data from DB
        loadScholarshipsFromDB();

        // 2. Setup ComboBox
        comboLocation.getItems().addAll("All Locations", "Cebu City", "Manila", "Davao");
        comboLocation.getSelectionModel().selectFirst();

        // 3. Setup Table Mapping (FIXED: No more red text)
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colSponsor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSponsor()));
        colGrade.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGradeRequired()));
        colLocation.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));

        // 4. Wrap in FilteredList
        FilteredList<Scholarship> filteredData = new FilteredList<>(masterData, p -> true);

        // 5. Listeners
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        comboLocation.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        sliderGrade.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblGradeValue.setText(String.format("%.0f", newVal));
            updateFilter(filteredData);
        });

        tableScholarships.setItems(filteredData);
    }

    private void loadScholarshipsFromDB() {
        masterData.clear();
        String query = "SELECT * FROM scholarships WHERE status = 'Active'";

        try (Connection conn = mySQLConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                masterData.add(new Scholarship(
                        rs.getInt("scholarshipID"),
                        rs.getString("name"),
                        rs.getString("sponsor"),
                        rs.getDouble("gradeRequired"),
                        rs.getString("location")
                ));
            }

        } catch (Exception e) {
            System.err.println("Database fetch error in ScholarshipListController");
            e.printStackTrace();
        }
    }

    private void updateFilter(FilteredList<Scholarship> data) {
        data.setPredicate(s -> {
            String search = txtSearch.getText().toLowerCase();
            boolean matchesSearch = s.getName().toLowerCase().contains(search) ||
                    s.getSponsor().toLowerCase().contains(search);

            boolean matchesLocation = comboLocation.getValue() == null ||
                    comboLocation.getValue().equals("All Locations") ||
                    s.getLocation().equals(comboLocation.getValue());

            // Shows scholarships where your grade (Slider) is >= requirement
            boolean matchesGrade = sliderGrade.getValue() >= s.getGradeRequired();

            return matchesSearch && matchesLocation && matchesGrade;
        });
    }
}