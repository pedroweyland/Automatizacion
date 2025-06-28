package com.cartadespido.automatizacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 450);

        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/progress-bar.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/combo-box.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/drop-zone.css").toExternalForm());

        Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 12);

        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/images.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}