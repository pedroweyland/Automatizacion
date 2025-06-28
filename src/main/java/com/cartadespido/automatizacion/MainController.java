package com.cartadespido.automatizacion;

import com.cartadespido.automatizacion.automation.AutomationRunner;
import com.cartadespido.automatizacion.model.Worker;
import com.cartadespido.automatizacion.ui.AlertControl;
import com.cartadespido.automatizacion.ui.WindowController;
import com.cartadespido.automatizacion.util.FileLoader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {
    @FXML private Button btnIniciar;
    @FXML private Label statusLabel;

    @FXML private BorderPane titleBar;
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;

    @FXML private VBox dropZone;

    @FXML private ProgressBar progressBar;
    @FXML private Label progressText;

    @FXML private ComboBox<Integer> horaCombo;
    @FXML private ComboBox<Integer> minutoCombo;

    private List<Worker> workers = new ArrayList<>();

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            WindowController.enableDrag(titleBar, stage);
        });

        btnClose.setOnAction(e -> WindowController.close(btnClose));
        btnMinimize.setOnAction(e -> WindowController.minimize(btnMinimize));

        // Zona de drop
        dropZone.setOnMouseClicked(event -> abrirFileChooser());
        dropZone.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File archivo = db.getFiles().get(0);
                procesarArchivo(archivo);
            }
            event.setDropCompleted(true);
            event.consume();
        });

        for (int i = 0; i < 24; i++) horaCombo.getItems().add(i);
        for (int i = 0; i < 60; i++) minutoCombo.getItems().add(i); // De a 5 minutos
        horaCombo.setValue(LocalTime.now().getHour());
        minutoCombo.setValue(0);
    }

    private void abrirFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            procesarArchivo(archivo);
        } else {
            statusLabel.setText("⚠ Archivo no seleccionado");
        }
    }

    private void procesarArchivo(File archivo) {
        try {

            workers = FileLoader.cargarWorkers(archivo);
            statusLabel.setText("✅ " + workers.size() + " trabajadores cargados");
            progressBar.setProgress(0);
            progressText.setText("");

        } catch (IllegalArgumentException e) {
            AlertControl alertControl = new AlertControl();
            Alert alert = alertControl.createErrorAlert("Error de lectura CSV", "Error al leer el archivo CSV", e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            statusLabel.setText("⚠ Error al leer el archivo");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onIniciar() {
        if (workers == null || workers.isEmpty()) {
            statusLabel.setText("⚠ Primero cargá el archivo CSV");
            return;
        }
        progressBar.setProgress(0);
        progressBar.setProgress(0);
        progressText.setText("");

        AutomationRunner runner = new AutomationRunner(
                workers,
                mensaje -> statusLabel.setText(mensaje),
                (porcentaje, texto) -> {
                    animateProgress(porcentaje);
                    progressText.setText(texto);
                }
        );
        runner.start();
    }

    private void animateProgress(double targetProgress) {
        double currentProgress = progressBar.getProgress();

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), currentProgress)
                ),
                new KeyFrame(
                        Duration.seconds(0.5),
                        new KeyValue(progressBar.progressProperty(), targetProgress)
                )
        );
        timeline.play();
    }

    @FXML
    private void onProgramar() {
        if (workers == null || workers.isEmpty()) {
            statusLabel.setText("⚠ Primero cargá el archivo CSV");
            return;
        }

        int hora = horaCombo.getValue();
        int minuto = minutoCombo.getValue();

        LocalTime horaObjetivo = LocalTime.of(hora, minuto);
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime objetivo = LocalDateTime.of(LocalDate.now(), horaObjetivo);

        if (objetivo.isBefore(ahora)) {
            objetivo = objetivo.plusDays(1); // Si ya pasó, lo programa para mañana
        }

        java.time.Duration delay = java.time.Duration.between(ahora, objetivo);

        long delayMs = delay.toMillis();

        statusLabel.setText("⏰ Automatización programada para las " + hora + ":" + String.format("%02d", minuto));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    AutomationRunner runner = new AutomationRunner(
                            workers,
                            mensaje -> statusLabel.setText(mensaje),
                            (porcentaje, texto) -> {
                                animateProgress(porcentaje);
                                progressText.setText(texto);
                            }
                    );
                    runner.start();
                });
            }
        }, delayMs);
    }
}
