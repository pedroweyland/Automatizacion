package com.cartadespido.automatizacion.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AutomationUtils {

    public static void rellenarFormularioByName(WebDriverWait wait, String name, String value) {
        WebElement inputElement = wait.until(ExpectedConditions.elementToBeClickable(By.name(name)));
        inputElement.clear();
        inputElement.sendKeys(value);
    }

    public static void rellenarSelectByName(WebDriverWait wait, String name, String value) {
        Select inputElement = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name(name))));
        inputElement.selectByValue(value);
    }

    public static String formatearRutConGuion(Long rutCompleto) {
        String rutStr = rutCompleto.toString();
        if (rutStr.length() < 2) {
            throw new IllegalArgumentException("RUT del empleador no reconocido");
        }
        String cuerpo = rutStr.substring(0, rutStr.length() - 1);
        String dv = rutStr.substring(rutStr.length() - 1);
        return cuerpo + "-" + dv;
    }

    public static void safeSleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
