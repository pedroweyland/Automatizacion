package com.cartadespido.automatizacion.automation;

import com.cartadespido.automatizacion.model.Worker;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.cartadespido.automatizacion.automation.AutomationUtils.*;

public class FillFormsWorkers {

    public void fillForm(Worker worker, WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {

            fillFormWorkers(worker, wait);
            fillFormDetails(worker, wait, driver);
            fillFormAmount(worker, wait);
            fillFormQuotes(worker, wait);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        } finally {
            safeSleep(2);
        }
    }

    private void fillFormWorkers(Worker worker, WebDriverWait wait) {
        WebElement sexoRadio, submitButton;

        rellenarFormularioByName(wait, "RutTrab", worker.getRutTrab().toString());

        rellenarFormularioByName(wait, "Nom", worker.getNom());

        rellenarFormularioByName(wait, "ApePat", worker.getApePat());

        rellenarFormularioByName(wait, "ApeMat", worker.getApeMat());

        rellenarFormularioByName(wait, "SelDom", worker.getSelDom());

        rellenarSelectByName(wait, "CodCom", worker.getCodCom().toString());

        String sexoValue = String.valueOf(worker.getSex()); // 1 = Masculino, 2 = Femenino
        sexoRadio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@name='Sex' and @value='" + sexoValue + "']")
        ));
        sexoRadio.click();

        rellenarSelectByName(wait, "nac", worker.getNac().toString());

        submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("Siguiente")));
        submitButton.click();
    }

    private void fillFormDetails(Worker worker, WebDriverWait wait, WebDriver driver) {
        WebElement formaRadio, fechaInicioInput, fechaTerminoInput, fechaComisionInput;
        Select causalInput;
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String formaValue = String.valueOf(worker.getForma());
        formaRadio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@name='Forma' and @value='" + formaValue + "']")
        ));
        formaRadio.click();

        fechaInicioInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("FecIniCon")));
        js.executeScript("arguments[0].value = arguments[1];", fechaInicioInput, worker.getFecIniCon());

        fechaTerminoInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("FecTerCon")));
        js.executeScript("arguments[0].value = arguments[1];", fechaTerminoInput, worker.getFecTerCon());

        fechaComisionInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("FecComDes")));
        js.executeScript("arguments[0].value = arguments[1];", fechaComisionInput, worker.getFecComDes());

        if (!worker.getOfi().isBlank() && worker.getForma() == 1) {
            rellenarFormularioByName(wait, "Ofi", worker.getOfi());
        }

        //rellenarSelectByName(wait, "Causal", worker.getCausal());
        causalInput = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("Causal"))));
        causalInput.selectByValue(worker.getCausal());

        rellenarFormularioByName(wait, "Motivo", worker.getMotivo());

    }

    private void fillFormAmount(Worker worker, WebDriverWait wait) {

        rellenarFormularioByName(wait, "AnoSer", worker.getAnoSer().toString());

        rellenarFormularioByName(wait, "Aviso", worker.getAviso().toString());
    }

    private void fillFormQuotes(Worker worker, WebDriverWait wait) {

        rellenarFormularioByName(wait, "Prev", worker.getPrev());

        String docValue = String.valueOf(worker.getDoc());
        WebElement docInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@name='Doc' and @value='" + docValue + "']")
        ));
        docInput.click();
    }

}
