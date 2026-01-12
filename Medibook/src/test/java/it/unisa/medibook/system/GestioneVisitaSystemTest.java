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

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        effettuaLogin("rossi@medibook.it", "password");
    }

    private void effettuaLogin(String email, String password) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/medico"));
    }

    @Test
    @Order(1)
    @DisplayName("TC_GV_1: Esecuzione Visita (Cambio Stato in EFFETTUATA)")
    public void testEsecuzioneVisita() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Clicca sul pulsante "Esegui"
        WebElement btnEsegui = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-esegui")));
        btnEsegui.click();

        // 2. SOLUZIONE TIMEOUT MODALE: Aspettiamo che il modale esista e che la classe CSS cambi
        // Usiamo un selettore CSS che punta direttamente alla classe attiva
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#modalConferma.active")));

        // 3. Clicca sul tasto di conferma finale nel modale
        WebElement btnConferma = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnConfermaFinale")));
        btnConferma.click();

        // 4. ORACOLO: Verifica la presenza del badge di stato aggiornato
        // Invece di urlContains, verifichiamo la presenza fisica del badge nella pagina ricaricata
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("bg-warning")));
        assertTrue(driver.getPageSource().contains("Serve Referto"));
    }

    @Test
    @Order(2)
    @DisplayName("TC_GV_2: Annullamento Visita (Cambio Stato in ANNULLATA)")
    public void testAnnullamentoVisita() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Clicca sul pulsante "Annulla"
        WebElement btnAnnulla = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-annulla")));
        btnAnnulla.click();

        // 2. Aspettiamo modale attivo
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#modalConferma.active")));

        // 3. Conferma
        driver.findElement(By.id("btnConfermaFinale")).click();

        // 4. ORACOLO AGGIORNATO:
        // Aspettiamo che appaia il badge rosso nello storico (bg-danger)
        WebElement badgeAnnullata = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("bg-danger")));

        // Pulizia del testo: rimuoviamo spazi bianchi e verifichiamo il contenuto
        String testoEffettivo = badgeAnnullata.getText().trim();

        // Usiamo assertTrue con contains per evitare fallimenti dovuti a icone o spazi invisibili
        assertTrue(testoEffettivo.contains("Annullata"),
                "Il badge dovrebbe contenere il testo 'Annullata'. Trovato invece: [" + testoEffettivo + "]");
    }
    @Test
    @Order(3)
    @DisplayName("TC_VIS_3: Modifica Visita già processata (CONCLUSA)")
    public void testModificaVisitaConclusa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Cerchiamo il badge "Conclusa" nella tabella dello storico
        // Il selettore punta allo span con classe bg-success che contiene il testo
        WebElement badgeConclusa = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(@class, 'bg-success') and contains(text(), 'Conclusa')]")));

        // Risaliamo alla riga (tr) della tabella
        WebElement rigaConclusa = badgeConclusa.findElement(By.xpath("./ancestor::tr"));

        // 2. ORACOLO: Verifichiamo che in questa riga NON esistano pulsanti di gestione (Esegui/Annulla)
        // Questi pulsanti hanno classe 'btn-esegui' o 'btn-annulla'
        List<WebElement> pulsantiGestione = rigaConclusa.findElements(By.className("action-btn"));

        // Filtriamo per assicurarci che tra i pulsanti non ci siano quelli di gestione stato
        boolean pulsantiIllegaliPresenti = pulsantiGestione.stream()
                .anyMatch(btn -> btn.getAttribute("class").contains("btn-esegui")
                        || btn.getAttribute("class").contains("btn-annulla"));

        assertFalse(pulsantiIllegaliPresenti, "ERRORE: Una visita conclusa non deve mostrare opzioni di modifica stato.");

        // 3. ORACOLO EXTRA: Verifica che esista invece il tasto "Vedi Referto"
        WebElement btnVediReferto = rigaConclusa.findElement(By.className("btn-vedi"));
        assertTrue(btnVediReferto.isDisplayed(), "Dovrebbe essere presente il tasto 'Vedi Referto'.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}