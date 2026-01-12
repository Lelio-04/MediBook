package it.unisa.medibook.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompilazioneRefertoSystemTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Login come Segreteria Prenotazioni (admin) per accedere ai percorsi /segreteria-prenotazioni/
        effettuaLogin("segreteria.prenotazioni@medibook.it", "admin");
    }

    private void effettuaLogin(String email, String password) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/segreteria-prenotazioni/dashboard"));
    }

    @Test
    @Order(1)
    @DisplayName("TC_REF_1: Stato visita non valido (PRENOTATA)")
    public void testRefertoStatoNonValido() {
        // L'ID 2 nel seeder è p701 ed è in stato PRENOTATA
        driver.get(baseUrl + "/segreteria-prenotazioni/referto/nuovo?id=2");

        // Il controller rimanda alla dashboard con parametro errore=StatoNonValido
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("StatoNonValido"));

        assertTrue(driver.getCurrentUrl().contains("dashboard"), "Dovrebbe essere tornato in dashboard");
    }

    @Test
    @Order(2)
    @DisplayName("TC_REF_2: Referto Vuoto")
    public void testRefertoVuoto() {
        // Naviga alla visita 3 (p702 nel Seeder - EFFETTUATA)
        driver.get(baseUrl + "/segreteria-prenotazioni/referto/nuovo?id=3");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement areaReferto = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("contenuto")));
        areaReferto.clear();

        // Rimuoviamo il vincolo browser per permettere al form di arrivare al Controller
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].removeAttribute('required');", areaReferto);

        // Clicchiamo sul tasto Salva (usiamo il selettore della classe del pulsante)
        driver.findElement(By.cssSelector("button.btn-success")).click();

        // ORACOLO: Ora che la JSP ha il div alert-danger, Selenium lo troverà immediatamente!
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertTrue(alert.getText().toLowerCase().contains("obbligatorio"),
                "L'alert dovrebbe mostrare l'errore del Service. Trovato: " + alert.getText());
    }

    @Test
    @Order(3)
    @DisplayName("TC_REF_3: Salvataggio Corretto")
    public void testSalvataggioCorretto() {
        // ID 4 -> p703 nel Seeder (EFFETTUATA)
        driver.get(baseUrl + "/segreteria-prenotazioni/referto/nuovo?id=4");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement areaReferto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("contenuto")));

        areaReferto.sendKeys("Esame obiettivo negativo. Paziente in ottima salute.");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/segreteria-prenotazioni/dashboard"));
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
        assertTrue(successMsg.getText().contains("Referto salvato"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
