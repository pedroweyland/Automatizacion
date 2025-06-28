package com.cartadespido.automatizacion.automation;

import com.cartadespido.automatizacion.model.Worker;
import javafx.application.Platform;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AutomationRunner {

    private final List<Worker> workers;
    private final Consumer<String> statusUpdater;
    private final BiConsumer<Double, String> progressUpdater;

    public AutomationRunner(List<Worker> workers,
                            Consumer<String> statusUpdater,
                            BiConsumer<Double, String> progressUpdater) {
        this.workers = workers;
        this.statusUpdater = statusUpdater;
        this.progressUpdater = progressUpdater;
    }

    public void start() {
        new Thread(() -> {
            WebDriver driver = null;
            try {
                Platform.runLater(() -> statusUpdater.accept("⏳ Ejecutando..."));
                Navigator navigator = new Navigator();
                FillFormsLogin loginForm = new FillFormsLogin();
                FillFormsWorkers fillForms = new FillFormsWorkers();

                driver = loginForm.loginForm();
                String currentUrl = driver.getCurrentUrl();

                boolean esPrimerTrabajador = true;
                int total = workers.size();

                for (int i = 0; i < total; i++) {
                    Worker worker = workers.get(i);
                    System.out.println("Procesando: " + worker.getNom());

                    if (!esPrimerTrabajador)
                        driver.navigate().to(currentUrl);

                    driver = navigator.navToForm(driver, worker.getRutEmpleador());
                    fillForms.fillForm(worker, driver);

                    int progreso = i + 1;

                    String texto = progreso + " / " + total + " trabajadores procesados";
                    double porcentaje = (double) progreso / total;
                    Platform.runLater(() -> progressUpdater.accept(porcentaje, texto));

                    esPrimerTrabajador = false;
                    Thread.sleep(1000);
                }

                Platform.runLater(() -> statusUpdater.accept("✅ Automatización completa"));
            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> progressUpdater.accept(0.0, ""));
                Platform.runLater(() -> statusUpdater.accept("⚠ Error de entrada: " + e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> progressUpdater.accept(0.0, ""));
                Platform.runLater(() -> statusUpdater.accept("⚠ Error durante la automatización"));
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }).start();
    }
}
