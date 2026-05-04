package main.java;

import Serialization.FileHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.User;
import main.java.controller.DashboardController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        User savedUser = FileHandler.deserialize("session.ser");

        if (savedUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard-view.fxml"));
            Parent root = loader.load();

            DashboardController dashboard = loader.getController();
            dashboard.setUserData(savedUser.getName());

            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle("Scholarsheesh - Dashboard");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainmenu-view.fxml"));
            stage.setScene(new Scene(loader.load(), 1440, 1024));
            stage.setTitle("Scholarsheesh - Login");
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}