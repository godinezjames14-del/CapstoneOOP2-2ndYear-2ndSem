package main.java.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class DashboardController {

    @FXML
    private Label labelUserName;

    @FXML
    private ImageView profileImageView;

    /**
     * Call this method when the dashboard loads to set the user's name dynamically.
     */
    public void setUserData(String name) {
        if (name != null && !name.isEmpty()) {
            labelUserName.setText(name.toUpperCase());
        }
    }

    @FXML
    public void initialize() {

    }
}