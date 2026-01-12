package it.unisa.medibook.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GestioneVisitaSystemTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8080";
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Inizializzo qui per riutilizzarlo
        effettuaLogin("rossi@medibook.it", "password");
    }

    private void effettuaLogin(String email, String password) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);

        // Uso JS click per sicurezza nel login
        WebElement btnLogin = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnLogin);

        wait.until(ExpectedConditions.urlContains("/medico"));
    }

    @Test
    @Order(1)
    @DisplayName("TC_GV_1: Esecuzione Visita (Cambio Stato in EFFETTUATA)")
    public void testEsecuzioneVisita() {
        // 1. Clicca sul pulsante "Esegui"
        // Usiamo JavascriptExecutor: è più robusto se ci sono elementi sovrapposti
        WebElement btnEsegui = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-esegui")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnEsegui);

        // 2. CORREZIONE: Aspettiamo la VISIBILITÀ dell'elemento per ID
        // Non controlliamo la classe specifica (.active o .show), basta che sia visibile all'utente.
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalConferma")));

        // 3. Clicca sul tasto di conferma finale nel modale
        WebElement btnConferma = modal.findElement(By.id("btnConfermaFinale"));
        wait.until(ExpectedConditions.elementToBeClickable(btnConferma));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConferma);

        // 4. ORACOLO: Verifica badge aggiornato
        // Aspettiamo che il vecchio pulsante "muoia" (pagina aggiornata o ricaricata)
        wait.until(ExpectedConditions.stalenessOf(btnEsegui));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("bg-warning")));
        assertTrue(driver.getPageSource().contains("Serve Referto"));
    }

    @Test
    @Order(2)
    @DisplayName("TC_GV_2: Annullamento Visita (Cambio Stato in ANNULLATA)")
    public void testAnnullamentoVisita() {
        // 1. Clicca sul pulsante "Annulla"
        WebElement btnAnnulla = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-annulla")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnAnnulla);

        // 2. Aspettiamo modale visibile (stessa correzione di sopra)
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalConferma")));

        // 3. Conferma nel modale
        WebElement btnConferma = modal.findElement(By.id("btnConfermaFinale"));
        wait.until(ExpectedConditions.elementToBeClickable(btnConferma));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConferma);

        // 4. ORACOLO
        wait.until(ExpectedConditions.stalenessOf(btnAnnulla)); // Aspetta refresh

        WebElement badgeAnnullata = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("bg-danger")));
        String testoEffettivo = badgeAnnullata.getText().trim();

        assertTrue(testoEffettivo.contains("Annullata"),
                "Il badge dovrebbe contenere 'Annullata'. Trovato: " + testoEffettivo);
    }

    @Test
    @Order(3)
    @DisplayName("TC_VIS_3: Modifica Visita già processata (CONCLUSA)")
    public void testModificaVisitaConclusa() {
        // 1. Cerchiamo il badge "Conclusa"
        WebElement badgeConclusa = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(@class, 'bg-success') and contains(text(), 'Conclusa')]")));

        WebElement rigaConclusa = badgeConclusa.findElement(By.xpath("./ancestor::tr"));

        // 2. ORACOLO: Verifica assenza bottoni azione
        List<WebElement> pulsantiGestione = rigaConclusa.findElements(By.className("action-btn"));

        boolean pulsantiIllegaliPresenti = pulsantiGestione.stream()
                .anyMatch(btn -> btn.getAttribute("class").contains("btn-esegui")
                        || btn.getAttribute("class").contains("btn-annulla"));

        assertFalse(pulsantiIllegaliPresenti, "ERRORE: Una visita conclusa non deve mostrare opzioni di modifica stato.");

        // 3. ORACOLO EXTRA: Verifica presenza tasto "Vedi Referto"
        List<WebElement> btnVedi = rigaConclusa.findElements(By.className("btn-vedi"));
        assertFalse(btnVedi.isEmpty(), "Il pulsante 'Vedi Referto' dovrebbe esistere");
        assertTrue(btnVedi.get(0).isDisplayed(), "Il pulsante 'Vedi Referto' dovrebbe essere visibile.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}