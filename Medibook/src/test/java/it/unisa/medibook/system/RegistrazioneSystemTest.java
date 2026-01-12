package it.unisa.medibook.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrazioneSystemTest {

    private WebDriver driver;
    private final String regUrl = "http://localhost:8080/registrazione";
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("TC_REG_1: Formato Email Errato")
    public void testFormatoEmailErrato() {
        driver.get(regUrl);
        disabilitaValidazioneClient();

        compilaCampiBaseRandom(); // Dati nuovi per evitare conflitti CF/Email

        WebElement emailInput = driver.findElement(By.name("email"));
        emailInput.clear();
        emailInput.sendKeys("email_sbagliata"); // Senza @

        driver.findElement(By.name("password")).sendKeys("password123");
        clickRegistrati();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        // Controlliamo "email" OR "formato"
        assertTrue(error.getText().toLowerCase().contains("email") || error.getText().toLowerCase().contains("formato"),
                "Errore atteso sul formato email. Trovato: " + error.getText());
    }

    @Test
    @Order(2)
    @DisplayName("TC_REG_2: Email Duplicata")
    public void testEmailDuplicata() {
        driver.get(regUrl);

        // 1. Dati Statici
        // Email: ESISTENTE (dal Seeder "Mario Rossi")
        String emailEsistente = "mariorossi@gmail.com";
        // CF: NUOVO (per evitare che l'errore sia sul CF invece che sull'email)
        String cfNuovo = "LBNGNN80A01H501Z";

        // 2. Compilazione
        compilaForm("Nuovo", "Utente", cfNuovo, "3339988776", emailEsistente, "Password123");
        clickRegistrati();

        // 3. Verifica
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        String msg = error.getText().toLowerCase();

        assertTrue(msg.contains("email") && (msg.contains("presente") || msg.contains("esistente") || msg.contains("uso")),
                "Dovrebbe segnalare email duplicata. Trovato: " + error.getText());
    }

    @Test
    @Order(3)
    @DisplayName("TC_REG_3: Codice Fiscale Errato")
    public void testCodiceFiscaleErrato() {
        driver.get(regUrl);
        disabilitaValidazioneClient();

        driver.findElement(By.name("nome")).sendKeys("Test");
        driver.findElement(By.name("cognome")).sendKeys("User");
        driver.findElement(By.name("telefono")).sendKeys("3331234567");
        driver.findElement(By.name("email")).sendKeys("nuovoutente" + System.currentTimeMillis() + "@test.it");

        // Input errato (corto)
        driver.findElement(By.name("codiceFiscale")).sendKeys("RSSMRA80A01");
        driver.findElement(By.name("password")).sendKeys("password123");

        clickRegistrati();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertTrue(error.getText().toLowerCase().contains("codice fiscale") || error.getText().toLowerCase().contains("lunghezza") || error.getText().toLowerCase().contains("non valido"),
                "Dovrebbe segnalare CF non valido. Trovato: " + error.getText());
    }

    @Test
    @Order(4)
    @DisplayName("TC_REG_4: Lunghezza Password non valida")
    public void testPasswordCorta() {
        driver.get(regUrl);
        disabilitaValidazioneClient();

        compilaCampiBaseRandom(); // CF e Email nuovi

        // Input errato (< 8 caratteri)
        driver.findElement(By.name("password")).sendKeys("mario");

        clickRegistrati();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertTrue(error.getText().toLowerCase().contains("password") || error.getText().toLowerCase().contains("lunghezza") || error.getText().contains("8"),
                "Dovrebbe segnalare password corta. Trovato: " + error.getText());
    }

    @Test
    @Order(5)
    @DisplayName("TC_REG_5: Registrazione con Successo")
    public void testRegistrazioneSuccesso() {
        driver.get(regUrl);

        compilaCampiBaseRandom(); // Genera tutto nuovo
        driver.findElement(By.name("password")).sendKeys("PasswordSicura123");

        clickRegistrati();

        // Verifica redirect/successo
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name("nome")));
        } catch (TimeoutException e) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
        }
    }

    // --- HELPER METHODS ---

    /**
     * Genera CF e Email univoci per evitare conflitti con esecuzioni precedenti
     */
    private void compilaCampiBaseRandom() {
        long timestamp = System.currentTimeMillis();
        Random rand = new Random();

        // Genera un CF valido sintatticamente ma con numeri random per unicità
        // Formato: AAAA AA 00 A 00 A 000 A (16 chars)
        String randomCF = "TESTCF" + (10 + rand.nextInt(89)) + "A" + (10 + rand.nextInt(20)) + "H" + (100 + rand.nextInt(800)) + "X";

        driver.findElement(By.name("nome")).sendKeys("Test" + timestamp);
        driver.findElement(By.name("cognome")).sendKeys("User");
        driver.findElement(By.name("codiceFiscale")).sendKeys(randomCF);
        driver.findElement(By.name("telefono")).sendKeys("3330000000");
        driver.findElement(By.name("email")).sendKeys("test" + timestamp + "@email.it");
    }
    private void compilaForm(String nome, String cognome, String cf, String telefono, String email, String password) {
        driver.findElement(By.name("nome")).clear();
        driver.findElement(By.name("nome")).sendKeys(nome);

        driver.findElement(By.name("cognome")).clear();
        driver.findElement(By.name("cognome")).sendKeys(cognome);

        driver.findElement(By.name("codiceFiscale")).clear();
        driver.findElement(By.name("codiceFiscale")).sendKeys(cf);

        driver.findElement(By.name("telefono")).clear();
        driver.findElement(By.name("telefono")).sendKeys(telefono);

        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys(email);

        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(password);
    }

    private void clickRegistrati() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    private void disabilitaValidazioneClient() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var form = document.querySelector('form');" +
                        "form.noValidate = true;" +
                        "var inputs = form.querySelectorAll('input');" +
                        "inputs.forEach(i => {" +
                        "  i.removeAttribute('required');" +
                        "  i.removeAttribute('minlength');" +
                        "  i.removeAttribute('maxlength');" +
                        "  i.removeAttribute('pattern');" +
                        "  if(i.type === 'email') i.type = 'text';" +
                        "});"
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}