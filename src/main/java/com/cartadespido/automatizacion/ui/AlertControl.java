package com.cartadespido.automatizacion.ui;

import javafx.scene.control.Alert;

public class AlertControl {

    public Alert createErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/alert.css").toExternalForm()
        );
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

}
