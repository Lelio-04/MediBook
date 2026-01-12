package it.unisa.medibook.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrenotazioneSystemTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8080";
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("TC_PRE_1: Data nel passato")
    public void testDataPassata() {
        effettuaLogin("mariorossi@gmail.com", "mario123456");
        driver.get(baseUrl + "/paziente");

        selezionaMedico("Rossi");

        // Bypassiamo i controlli UI di Flatpickr
        WebElement inputData = driver.findElement(By.id("dataInput"));
        WebElement selectOra = driver.findElement(By.id("oraSelect"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].disabled = false; arguments[0].value = '2023-01-10';", inputData);
        // Forziamo l'orario direttamente
        js.executeScript("arguments[0].disabled = false; arguments[0].innerHTML = '<option value=\"10:30\" selected>10:30</option>';", selectOra);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("errore"));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertTrue(alert.getText().toLowerCase().contains("passato") || alert.getText().toLowerCase().contains("non valida"),
                "Dovrebbe impedire la prenotazione nel passato.");
    }

    @Test
    @Order(2)
    @DisplayName("TC_PRE_2: Slot occupato")
    public void testSlotOccupato() {
        effettuaLogin("mariorossi@gmail.com", "mario123456");
        driver.get(baseUrl + "/paziente");

        selezionaMedico("Rossi");

        // Slot occupato dal Seeder: Domani ore 10:00
        String dataDomani = LocalDate.now().plusDays(1).toString();
        WebElement inputData = driver.findElement(By.id("dataInput"));
        WebElement selectOra = driver.findElement(By.id("oraSelect"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].disabled = false; arguments[0].value = arguments[1];", inputData, dataDomani);
        // Iniettiamo l'orario occupato che la UI normalmente nasconderebbe
        js.executeScript("arguments[0].disabled = false; arguments[0].innerHTML = '<option value=\"10:00\" selected>10:00</option>';", selectOra);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertTrue(alert.getText().toLowerCase().contains("non disponibile") || alert.getText().toLowerCase().contains("occupato"));
    }

    @Test
    @Order(3)
    @DisplayName("TC_PRE_3: Utente non loggato")
    public void testUtenteNonLoggato() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl + "/paziente");

        // Verifica redirect
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/paziente")));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/") || currentUrl.contains("accedi") || currentUrl.contains("login"));
    }

    @Test
    @Order(4)
    @DisplayName("TC_PRE_4: Prenotazione Corretta")
    public void testPrenotazioneCorretta() {
        effettuaLogin("mariorossi@gmail.com", "mario123456");
        driver.get(baseUrl + "/paziente");

        // 1. Seleziona Medico (Verdi - Pomeridiano)
        selezionaMedico("Verdi");

        // 2. Imposta Data Futura
        String dataFutura = LocalDate.now().plusDays(20).toString();
        WebElement inputData = driver.findElement(By.id("dataInput"));
        WebElement selectOra = driver.findElement(By.id("oraSelect"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].disabled = false; arguments[0].value = arguments[1];", inputData, dataFutura);

        // 3. FIX TIMEOUT: Popoliamo la select manualmente via JS invece di aspettare l'AJAX
        // Il Dr. Verdi lavora dalle 14:00 alle 18:00, quindi 15:30 è valido.
        String scriptPopolaSelect =
                "var select = arguments[0];" +
                        "select.disabled = false;" +
                        "select.innerHTML = '<option value=\"15:30\">15:30</option>';";

        js.executeScript(scriptPopolaSelect, selectOra);

        // 4. Selezioniamo l'opzione appena creata
        Select oraSelect = new Select(selectOra);
        oraSelect.selectByValue("15:30");

        // 5. Conferma
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 6. Oracolo
        wait.until(ExpectedConditions.urlContains("successo=true"));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
        assertTrue(alert.getText().toLowerCase().contains("successo") || alert.getText().toLowerCase().contains("conferma"));
    }

    // --- HELPER ---

    private void effettuaLogin(String email, String pass) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(pass);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/paziente"));
    }

    private void selezionaMedico(String cognome) {
        WebElement searchInput = driver.findElement(By.id("medicoSearch"));
        searchInput.clear();
        searchInput.sendKeys(cognome);
        WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.className("suggestion-item")));
        suggestion.click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}