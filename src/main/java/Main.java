package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/mainmenu-view.fxml")); // loader sa screen
        Scene scene = new Scene(loader.load(), 1440, 1024); // scene dimensi0ns
        stage.setTitle("Main.Main Menu");
        stage.setScene(scene); // kani kay mura rag katong intent.start or something limot ko
        stage.show(); // kani kay katong setView or something
    }

    public static void main(String[] args) {
        launch();
    }
}