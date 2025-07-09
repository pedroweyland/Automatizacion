package com.cartadespido.automatizacion.automation;

import com.cartadespido.automatizacion.model.Status;
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
    private final Runnable onFinish;
    private final Consumer<List<Worker>> resultUpdater;

    public AutomationRunner(List<Worker> workers,
                            Consumer<String> statusUpdater,
                            BiConsumer<Double, String> progressUpdater,
                            Runnable onFinish,
                            Consumer<List<Worker>> resultUpdater) {
        this.workers = workers;
        this.statusUpdater = statusUpdater;
        this.progressUpdater = progressUpdater;
        this.onFinish = onFinish;
        this.resultUpdater = resultUpdater;
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
                    Platform.runLater(() -> statusUpdater.accept("⏳ Procesando a " + worker.getNom() + " " + worker.getApePat() + " " + worker.getApeMat()));

                    if (!esPrimerTrabajador)
                        driver.navigate().to(currentUrl);

                    driver = navigator.navToForm(driver, worker.getRutEmpleador());
                    fillForms.fillForm(worker, driver);

                    int progreso = i + 1;

                    String texto = progreso + " / " + total + " trabajadores procesados";
                    double porcentaje = (double) progreso / total;

                    Platform.runLater(() -> progressUpdater.accept(porcentaje, texto));

                    esPrimerTrabajador = false;

                    worker.setStatus(Status.ENVIADO);
                    Thread.sleep(1000);
                }

                Platform.runLater(() -> {
                    statusUpdater.accept("✅ Automatización completa");
                    if (onFinish != null) onFinish.run();
                    if (resultUpdater != null) resultUpdater.accept(workers);
                });
            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> {
                    progressUpdater.accept(0.0, "");
                    statusUpdater.accept("⚠ Error de entrada: " + e.getMessage());
                    if (onFinish != null) onFinish.run();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressUpdater.accept(0.0, "");
                    statusUpdater.accept("⚠ Error durante la automatización");
                    if (onFinish != null) onFinish.run();
                });
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }).start();
    }
}
