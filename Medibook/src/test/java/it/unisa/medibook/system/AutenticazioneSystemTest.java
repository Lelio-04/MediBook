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
public class AutenticazioneSystemTest {

    private WebDriver driver;
    // URL aggiornata per riflettere il tuo controller
    private final String baseUrl = "http://localhost:8080/accedi";

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("TC_AUC_1: Login con Email Inesistente")
    public void testLoginEmailInesistente() {
        driver.get(baseUrl);

        driver.findElement(By.name("email")).sendKeys("lucaverdi@gmail.com");
        driver.findElement(By.name("password")).sendKeys("lucapassword123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 1. ATTESA: Aspetta che l'alert appaia (risolve il problema del valore vuoto)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        // 2. ORACOLO: Usiamo contains() perché nella JSP hai l'emoji "⚠️"
        String testoAlert = alert.getText();
        assertTrue(testoAlert.contains("Credenziali non valide!"),
                "L'alert dovrebbe contenere il messaggio di errore. Trovato: " + testoAlert);
    }

    @Test
    @Order(2)
    @DisplayName("TC_AUC_2: Login con Password Errata")
    public void testLoginPasswordErrata() {
        driver.get(baseUrl);

        // Pre-condizione: 'paziente@test.it' deve essere stato inserito dal DatabaseSeeder
        driver.findElement(By.name("email")).sendKeys("paziente@test.it");
        driver.findElement(By.name("password")).sendKeys("PasswordSbagliata789");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertTrue(alert.getText().contains("Credenziali non valide!"));
    }

    @Test
    @Order(3)
    @DisplayName("TC_AUC_3: Login con Successo")
    public void testLoginSuccesso() {
        driver.get(baseUrl);

        // Pre-condizione: assicurati che il Seeder abbia creato mariorossi@gmail.com / mario123456
        driver.findElement(By.name("email")).sendKeys("mariorossi@gmail.com");
        driver.findElement(By.name("password")).sendKeys("mario123456");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Aspettiamo che il controller effettui il redirect (cambio URL)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/paziente"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/paziente"),
                "Dopo il login l'utente dovrebbe essere in /paziente. URL attuale: " + currentUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}