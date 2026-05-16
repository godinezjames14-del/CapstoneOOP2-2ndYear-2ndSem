package main.java.controller;

import JDBC.mySQLConnection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import main.java.model.Scholarship;

import java.io.IOException;
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
    @FXML private TableColumn<Scholarship, Void> colAction;

    private ObservableList<Scholarship> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadScholarshipsFromDB();

        comboLocation.getItems().addAll("All Locations", "Cebu City", "Manila", "Davao");
        comboLocation.getSelectionModel().selectFirst();

        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colSponsor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSponsor()));
        colGrade.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getGradeRequired()));
        colLocation.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));

        createActionColumnButtons();

        FilteredList<Scholarship> filteredData = new FilteredList<>(masterData, p -> true);

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        comboLocation.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        sliderGrade.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblGradeValue.setText(String.format("%.0f", newVal));
            updateFilter(filteredData);
        });

        tableScholarships.setItems(filteredData);
    }

    private void createActionColumnButtons() {
        Callback<TableColumn<Scholarship, Void>, TableCell<Scholarship, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Scholarship, Void> call(final TableColumn<Scholarship, Void> param) {
                return new TableCell<>() {
                    private final Button btnView = new Button("View Info");

                    {
                        btnView.setStyle("-fx-background-color: #800000; -fx-text-fill: white; " +
                                "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;");
                        btnView.setPrefWidth(110);
                        btnView.setPrefHeight(30);

                        btnView.setOnAction(event -> {
                            Scholarship selected = getTableView().getItems().get(getIndex());
                            navigateToScholarshipTab(selected.getId());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnView);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    private void navigateToScholarshipTab(int scholarshipId) {
        try {
            // FIXED PATH: Pointing exactly to your working layout name
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scholarship-tab.fxml"));
            Parent root = loader.load();

            ScholarshipTabController controller = loader.getController();
            if (controller != null) {
                // Find your global container layout container from the scene tree stack
                BorderPane shellPane = (BorderPane) tableScholarships.getScene().lookup("#mainPane");

                if (shellPane != null) {
                    // Populate data, then set inner center panel workspace dynamically
                    controller.loadScholarshipData(shellPane, root, scholarshipId);
                } else {
                    // Fallback to safety if layout context changes unexpectedly
                    System.err.println("⚠️ Warning: mainPane not found via scene lookup.");
                }
            } else {
                System.err.println("❌ Error: fx:controller attribute missing inside scholarship-tab.fxml");
            }

        } catch (IOException e) {
            System.err.println("Navigation error loading scholarship requirements tab view.");
            e.printStackTrace();
        }
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

            boolean matchesGrade = sliderGrade.getValue() >= s.getGradeRequired();

            return matchesSearch && matchesLocation && matchesGrade;
        });
    }
}