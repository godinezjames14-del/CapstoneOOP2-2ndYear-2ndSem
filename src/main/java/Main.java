package main.java;

import Serialization.FileHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.User;
import main.java.controller.InterfaceController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        User savedUser = FileHandler.deserialize("session.ser");

        if (savedUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interface.fxml"));
            Parent root = loader.load();

            InterfaceController dashboard = loader.getController();
            dashboard.setUserData(savedUser.getName(), savedUser.getUserID(), savedUser.getRole());

            // Changed from 1440x1024 to 1280x800 for better compatibility
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("Scholarsheesh - Dashboard");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            // Standardized the login screen size as well
            stage.setScene(new Scene(loader.load(), 1280, 800));
            stage.setTitle("Scholarsheesh - Login");
        }

        // Optional: This ensures the window fits the user's specific screen resolution perfectly
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Added args to launch for best practice
    }
}