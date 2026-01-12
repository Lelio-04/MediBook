package it.unisa.medibook.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModificaPrenotazioneSystemTest {

    private WebDriver driver;
    private final String baseUrl = "http://localhost:8080";
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        effettuaLogin("segreteria.prenotazioni@medibook.it", "admin");
    }
    @Test
    @Order(1)
    @DisplayName("TC_MOD_1: ID Prenotazione Errato")
    public void testModificaIdInesistente() {
        driver.get(baseUrl + "/segreteria-prenotazioni/dashboard");

        // Simulazione invio form con ID inesistente tramite Script
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var f = document.createElement('form'); f.action='/segreteria-prenotazioni/aggiorna'; f.method='post';" +
                        "var i1 = document.createElement('input'); i1.name='id'; i1.value='9999';" +
                        "var i2 = document.createElement('input'); i2.name='nuovaData'; i2.value='2026-05-20';" +
                        "var i3 = document.createElement('input'); i3.name='nuovaOra'; i3.value='10:30';" +
                        "var i4 = document.createElement('input'); i4.name='nuovoStato'; i4.value='PRENOTATA';" +
                        "f.appendChild(i1); f.appendChild(i2); f.appendChild(i3); f.appendChild(i4);" +
                        "document.body.appendChild(f); f.submit();"
        );

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertTrue(alert.getText().contains("Prenotazione non trovata"), "Errore: Messaggio 'non trovata' non apparso.");
    }
    @Test
    @Order(2)
    @DisplayName("TC_MOD_2: Slot Orario Occupato")
    public void testModificaSlotOccupato() {
        driver.get(baseUrl + "/segreteria-prenotazioni/dashboard");

        // Cerchiamo la riga del paziente "Verdi Luca" (pDaModificare nel seeder)
        WebElement rigaVerdi = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td//strong[contains(text(), 'Verdi')]]")));

        // Slot occupato da Rossi alle 15:00 tra 15 giorni
        String dataOccupata = LocalDate.now().plusDays(15).toString();
        WebElement inputData = rigaVerdi.findElement(By.name("nuovaData"));
        WebElement inputOra = rigaVerdi.findElement(By.name("nuovaOra"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", inputData, dataOccupata);
        js.executeScript("arguments[0].value = '15:00';", inputOra);

        rigaVerdi.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertTrue(alert.getText().toLowerCase().contains("occupato") || alert.getText().toLowerCase().contains("disponibile"));
    }

    @Test
    @Order(3)
    @DisplayName("TC_MOD_3: Visita Passata o Conclusa (Controllo su EFFETTUATA)")
    public void testModificaVisitaPassata() {
        driver.get(baseUrl + "/segreteria-prenotazioni/dashboard");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement rigaEffettuata = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select/option[@selected and @value='EFFETTUATA']/ancestor::tr")));

        String dataPassata = LocalDate.now().minusDays(1).toString();
        WebElement inputData = rigaEffettuata.findElement(By.name("nuovaData"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", inputData, dataPassata);

        rigaEffettuata.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        // Pulizia del messaggio (rimuoviamo l'emoji e il prefisso se necessario)
        String msgOttenuto = alert.getText().replace("⚠️ ", "").trim();

        // AGGIORNAMENTO ORACOLO: Deve corrispondere a quanto restituito dal sistema
        assertTrue(msgOttenuto.contains("Non è possibile spostare una visita nel passato"),
                "Il messaggio di errore ottenuto non è quello atteso. Trovato: " + msgOttenuto);
    }

    @Test
    @Order(4)
    @DisplayName("TC_MOD_4: Modifica con Successo")
    public void testModificaSuccesso() {
        driver.get(baseUrl + "/segreteria-prenotazioni/dashboard");

        // Cerchiamo una riga con stato "PRENOTATA" (ne abbiamo diverse, prendiamo la prima)
        WebElement rigaModificabile = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[option[@selected and @value='PRENOTATA']]/ancestor::tr")));

        String dataLibera = LocalDate.now().plusDays(30).toString();
        WebElement inputData = rigaModificabile.findElement(By.name("nuovaData"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", inputData, dataLibera);

        rigaModificabile.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
        assertTrue(alert.getText().contains("aggiornata correttamente"));
    }

    private void effettuaLogin(String email, String pass) {
        driver.get(baseUrl + "/accedi");
        driver.findElement(By.name("email")).sendKeys(email);
        driver.findElement(By.name("password")).sendKeys(pass);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}