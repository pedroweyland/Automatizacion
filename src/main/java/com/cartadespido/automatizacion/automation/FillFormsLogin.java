package com.cartadespido.automatizacion.automation;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.cartadespido.automatizacion.automation.AutomationUtils.rellenarFormularioByName;
import static com.cartadespido.automatizacion.automation.AutomationUtils.safeSleep;

public class FillFormsLogin {

    private static final Dotenv dotenv = Dotenv.load();

    final String url = dotenv.get("URL");
    final String rut = dotenv.get("RUT");
    final String password = dotenv.get("PASSWORD");

    public WebDriver loginForm(){
        Logger.getLogger("org.openqa.selenium.devtools").setLevel(Level.OFF);
        Logger.getLogger("org.openqa.selenium.chromium.ChromiumDriver").setLevel(Level.OFF);
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");

        //String driverPath = new File("drivers/chromedriver.exe").getAbsolutePath();
        //System.setProperty("webdriver.chrome.driver", driverPath);

        WebDriver driver = new ChromeDriver();

        driver.get(url);
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {

            login(wait);
            safeSleep(1);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

        return driver;

    }

    private void login(WebDriverWait wait) {
        WebElement loginButtom, submitButton;
        safeSleep(2);

        loginButtom = wait.until(ExpectedConditions.elementToBeClickable(By.id("nuevaSesion")));
        loginButtom.click();

        rellenarFormularioByName(wait, "run", rut);
        rellenarFormularioByName(wait, "password", password);

        submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-submit")));
        submitButton.click();
    }
}
