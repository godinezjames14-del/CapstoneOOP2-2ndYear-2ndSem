package main.java.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void MainMenuScreen(MouseEvent event) {// copy pase rani sa mainmenu controller
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/mainmenu-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find register-view.fxml");
            e.printStackTrace();
        }
    }
}