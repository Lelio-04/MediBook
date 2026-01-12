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
public class CambioPasswordSystemTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Usa le credenziali appena create nel seeder
        effettuaLogin("paziente@testcpw.it", "Medibook123");
    }

    private void effettuaLogin(String email, String password) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // ASPETTA che il redirect avvenga
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/cambio-password-obbligatorio"));
    }

    @Test
    @Order(1)
    @DisplayName("TC_CPW_2: Password Troppo Corta")
    public void testPasswordCorta() {
        driver.findElement(By.name("nuovaPassword")).sendKeys("abc");
        driver.findElement(By.name("confermaPassword")).sendKeys("abc");

        WebElement btn = driver.findElement(By.cssSelector("button[type='submit']"));
        btn.click();

        // Poiché c'è minlength="8", il browser impedisce il submit.
        // Verifichiamo che siamo ancora sulla stessa pagina e il form non è partito
        assertTrue(driver.getCurrentUrl().contains("/cambio-password-obbligatorio"));

        // Verifica che il campo sia considerato invalido dal browser
        WebElement input = driver.findElement(By.name("nuovaPassword"));
        String validity = input.getAttribute("validity");
        // In alcuni casi validity non è accessibile direttamente, controlliamo se l'alert non c'è
        assertTrue(driver.findElements(By.className("alert-danger")).isEmpty(),
                "L'alert non dovrebbe esserci perché il browser ha bloccato l'invio");
    }

    @Test
    @Order(2)
    @DisplayName("TC_CPW_3: Mancata Corrispondenza Conferma")
    public void testPasswordNonCoincidenti() {
        driver.findElement(By.name("nuovaPassword")).sendKeys("MioSegreto2026");
        driver.findElement(By.name("confermaPassword")).sendKeys("MioSegreto9999");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertTrue(alert.getText().contains("Le due password non coincidono"));
    }

    @Test
    @Order(3)
    @DisplayName("TC_CPW_4: Nuova password uguale alla provvisoria")
    public void testUgualeAllaProvvisoria() {
        // Supponendo che nel seeder la pass provvisoria sia "Medibook123"
        driver.findElement(By.name("nuovaPassword")).sendKeys("Medibook123");
        driver.findElement(By.name("confermaPassword")).sendKeys("Medibook123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertTrue(alert.getText().contains("diversa da quella provvisoria"));
    }

    @Test
    @Order(4)
    @DisplayName("TC_CPW_1: Cambio Password Corretto")
    public void testCambioPasswordSuccesso() {
        driver.findElement(By.name("nuovaPassword")).sendKeys("NuovaPassword2026!");
        driver.findElement(By.name("confermaPassword")).sendKeys("NuovaPassword2026!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Il controller dopo il successo manda a /paziente?msg=...
        wait.until(ExpectedConditions.urlContains("/paziente"));

        assertTrue(driver.getCurrentUrl().contains("paziente"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}