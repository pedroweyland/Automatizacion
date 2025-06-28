package com.cartadespido.automatizacion.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.cartadespido.automatizacion.automation.AutomationUtils.formatearRutConGuion;
import static com.cartadespido.automatizacion.automation.AutomationUtils.safeSleep;

public class Navigator {

    public WebDriver navToForm(WebDriver driver, Long rutEmpleador) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {

            empleadorNav(wait, rutEmpleador);
            safeSleep(1);

            cartaNav(wait);
            safeSleep(1);

            driver = cartaAviso(driver, wait);

        } catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        } finally {
            safeSleep(1);
        }

        return driver;
    }

    private void empleadorNav(WebDriverWait wait, Long rutEmpleador) {
        WebElement empleadorPersonaButtom, empleadorButtom, empresaButton;

        empleadorButtom = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-empleador")));
        empleadorButtom.click();

        empleadorPersonaButtom = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='title accordion-title' and contains(., 'Empleador Persona Jurídica')]")
        ));
        empleadorPersonaButtom.click();

        String rutConGuion = formatearRutConGuion(rutEmpleador);

        String xpath = String.format("//button[@class='ui basic button' and contains(text(),'%s')]", rutConGuion);

        try {
            empresaButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            empresaButton.click();
        } catch (TimeoutException e) {
            System.out.println("No se encontro el rut del empleador: " + rutConGuion);
            throw new IllegalArgumentException("No se encontro la empresa con el RUT " + rutConGuion + " en la página. Verificá si es correcto.");
        }

    }

    private void cartaNav(WebDriverWait wait) {
        WebElement cartaDespidoButtom, contratosButtom;

        contratosButtom = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='title accordion-title' and contains(., 'Contratos de Trabajo y Despido')]")

        ));
        contratosButtom.click();

        cartaDespidoButtom = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[@class='clickable-text' and contains(., 'Carta electrónica de aviso de despido')]")
        ));
        cartaDespidoButtom.click();
    }

    private WebDriver cartaAviso(WebDriver driver, WebDriverWait wait) {

        driver = switchToNewTabAndCloseOld(driver, wait);

        WebElement cartaAvisoBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a.CA"))
        );
        cartaAvisoBtn.click();

        return driver;
    }

    private WebDriver switchToNewTabAndCloseOld(WebDriver driver, WebDriverWait wait) {
        String originalTab = driver.getWindowHandle();
        wait.until(d -> d.getWindowHandles().size() > 1);

        String newTab = driver.getWindowHandles().stream()
                .filter(handle -> !handle.equals(originalTab))
                .findFirst().orElseThrow(() -> new RuntimeException("No se encontró nueva pestaña"));

        driver.switchTo().window(originalTab).close();
        driver.switchTo().window(newTab);
        return driver;
    }
}
