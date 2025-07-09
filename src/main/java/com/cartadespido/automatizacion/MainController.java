package com.cartadespido.automatizacion;

import com.cartadespido.automatizacion.automation.AutomationRunner;
import com.cartadespido.automatizacion.model.Worker;
import com.cartadespido.automatizacion.repository.CsvWriter;
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

    @FXML private Button btnDescargar;

    private List<Worker> workers = new ArrayList<>();

    private boolean automatizacionCompleta = false;

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
        for (int i = 0; i < 60; i++) minutoCombo.getItems().add(i);
        horaCombo.setValue(LocalTime.now().getHour());
        minutoCombo.setValue(LocalTime.now().getMinute());

        btnDescargar.setVisible(false);
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
        btnDescargar.setVisible(false);

        AutomationRunner runner = new AutomationRunner(
                workers,
                mensaje -> statusLabel.setText(mensaje),
                (porcentaje, texto) -> {
                    animateProgress(porcentaje);
                    progressText.setText(texto);
                },
                () -> {
                    automatizacionCompleta = true;
                    btnDescargar.setVisible(true);
                },
                resultado -> {
                    this.workers = resultado;
                }
        );
        runner.start();
    }

    @FXML
    protected void onProgramar() {
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
        MainController controller = MainController.this;
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
                            },
                            () -> {
                                automatizacionCompleta = true;
                                btnDescargar.setVisible(true);
                            },
                            resultado -> {
                                controller.workers = resultado;
                            }
                    );
                    runner.start();
                });
            }
        }, delayMs);
    }

    @FXML
    private void onDescargar() {
        if (!automatizacionCompleta) {
            statusLabel.setText("⚠ Ejecutá una automatización antes de desacargar el output.");
            return;
        }

        File archivoDestino = FileLoader.cargarArchivoGuardar("Guardar output de trabajadores", "output_trabajadores");

        if (archivoDestino != null) {
            CsvWriter reader = new CsvWriter();
            reader.escribirEnCsv(workers, archivoDestino.getAbsolutePath());
            statusLabel.setText("💾 CSV guardado exitosamente");
        } else {
            statusLabel.setText("⚠ Guardado cancelado");
        }
    }

    private void abrirFileChooser() {
        File archivo = FileLoader.cargarArchivoBusqueda("Seleccionar archivo CSV");

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

}
