package com.cartadespido.automatizacion.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WindowController {
    public static void enableDrag(BorderPane titleBar, Stage stage) {
        final double[] offset = new double[2];
        titleBar.setOnMousePressed(event -> {
            offset[0] = event.getSceneX();
            offset[1] = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
    }

    public static void close(Button btn) {
        ((Stage) btn.getScene().getWindow()).close();
    }

    public static void minimize(Button btn) {
        ((Stage) btn.getScene().getWindow()).setIconified(true);
    }
}
