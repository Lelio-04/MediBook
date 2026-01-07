package it.unisa.medibook.modelService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void inviaEmailModifica(String destinatario, String oggetto,
                                   String nomePaziente, String nomeMedico,
                                   String nuovaData, String nuovaOra) {
        try {
            // 1. Creiamo un messaggio di tipo MIME (supporta HTML)
            MimeMessage message = mailSender.createMimeMessage();

            // "true" indica che stiamo inviando un messaggio multipart (con allegati o HTML)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tuaemail@gmail.com"); // Metti la tua email qui
            helper.setTo(destinatario);
            helper.setSubject(oggetto);

            // 2. Costruiamo il contenuto HTML
            String htmlContent = costruisciHtml(nomePaziente, nomeMedico, nuovaData, nuovaOra);

            // 3. Impostiamo il testo dicendo che è HTML (true)
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email HTML inviata a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Errore creazione email: " + e.getMessage());
        }
    }

    // Metodo privato che genera il design dell'email
    private String costruisciHtml(String nome, String medico, String data, String ora) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); overflow: hidden; }
                .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                .content { padding: 30px; color: #333333; line-height: 1.6; }
                .box-info { background-color: #e9f5ff; border-left: 5px solid #007bff; padding: 15px; margin: 20px 0; border-radius: 4px; }
                .footer { background-color: #333333; color: #dddddd; text-align: center; padding: 15px; font-size: 12px; }
                .btn { display: inline-block; background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 10px;}
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🏥 MediBook</h1>
                </div>
                <div class="content">
                    <h2>Aggiornamento Appuntamento</h2>
                    <p>Gentile <strong>%s</strong>,</p>
                    <p>Ti informiamo che il tuo appuntamento con il <strong>Dott. %s</strong> ha subito una variazione.</p>
                    
                    <div class="box-info">
                        <p style="margin: 5px 0;">📅 <strong>Nuova Data:</strong> %s</p>
                        <p style="margin: 5px 0;">🕒 <strong>Nuovo Orario:</strong> %s</p>
                    </div>

                    <p>Se hai bisogno di ulteriori chiarimenti, non esitare a contattare la nostra segreteria.</p>
                    
                    <center>
                        <a href="http://localhost:8080/accedi" class="btn">Accedi alla tua Area</a>
                    </center>
                </div>
                <div class="footer">
                    &copy; 2026 MediBook System - Tutti i diritti riservati.<br>
                    Questa è una comunicazione automatica, non rispondere a questa email.
                </div>
            </div>
        </body>
        </html>
        """.formatted(nome, medico, data, ora);
    }
}