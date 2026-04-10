import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent; // CRITICAL: Must be this import
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Leave empty for now
    }

    @FXML
    private void MainMenuScreen(MouseEvent event) { // Use MouseEvent, not ActionEvent
        try {
            // Load the FXML for the registration screen
            Parent registerRoot = FXMLLoader.load(getClass().getResource("mainmenu-view.fxml"));

            // Get the current window (Stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Swap the scene
            stage.setScene(new Scene(registerRoot));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find register-view.fxml");
            e.printStackTrace();
        }
    }
}