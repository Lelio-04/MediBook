package it.unisa.medibook.service;

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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tuaemail@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject(oggetto);

            String htmlContent = costruisciHtmlModifica(nomePaziente, nomeMedico, nuovaData, nuovaOra);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Email Modifica inviata a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Errore creazione email modifica: " + e.getMessage());
        }
    }

    public void inviaEmailConferma(String destinatario, String nomePaziente,
                                   String nomeMedico, String data, String ora) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tuaemail@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject("✅ Conferma Prenotazione - MediBook");

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; background-color: #f4f4f4; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .box-info { background-color: #e8f5e9; border-left: 5px solid #28a745; padding: 15px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #777; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Prenotazione Confermata!</h1>
                    </div>
                    <div class="content">
                        <p>Ciao <strong>%s</strong>,</p>
                        <p>La tua richiesta di prenotazione è andata a buon fine.</p>
                        
                        <div class="box-info">
                            <p>👨‍⚕️ <strong>Medico:</strong> Dott. %s</p>
                            <p>📅 <strong>Data:</strong> %s</p>
                            <p>🕒 <strong>Ora:</strong> %s</p>
                        </div>
                        <p>Ti ricordiamo di presentarti con 10 minuti di anticipo.</p>
                    </div>
                    <div class="footer">MediBook System - Email Automatica</div>
                </div>
            </body>
            </html>
            """.formatted(nomePaziente, nomeMedico, data, ora);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Errore invio email conferma: " + e.getMessage());
        }
    }

    public void inviaEmailBenvenuto(String destinatario, String nome, String cognome, String passwordTemp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tuaemail@gmail.com");
            helper.setTo(destinatario);
            helper.setSubject("🎉 Benvenuto in MediBook - Attivazione Account");

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Helvetica', sans-serif; background-color: #f0f2f5; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                    .header { background-color: #007bff; color: white; padding: 25px; text-align: center; }
                    .content { padding: 30px; color: #444; line-height: 1.6; }
                    .credentials-box { background-color: #f8f9fa; border: 2px dashed #007bff; padding: 20px; margin: 20px 0; border-radius: 8px; text-align: center; }
                    .warning { color: #856404; background-color: #fff3cd; padding: 10px; border-radius: 4px; font-size: 0.9em; margin-top: 15px; border-left: 5px solid #ffc107;}
                    .btn { display: inline-block; background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 50px; font-weight: bold; margin-top: 20px; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #888; background: #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Benvenuto in MediBook! 🏥</h1>
                    </div>
                    <div class="content">
                        <p>Gentile <strong>%s %s</strong>,</p>
                        <p>La segreteria ha creato con successo il tuo account personale.</p>
                        <p>Di seguito trovi le tue credenziali provvisorie per accedere al portale:</p>
                        
                        <div class="credentials-box">
                            <p>📧 <strong>Email (Username):</strong> %s</p>
                            <p>🔑 <strong>Password Provvisoria:</strong> <span style="font-size: 1.2em; font-weight: bold; color: #d9534f;">%s</span></p>
                        </div>

                        <div class="warning">
                            ⚠️ <strong>IMPORTANTE:</strong> Per motivi di sicurezza, al primo accesso ti verrà chiesto di cambiare questa password.
                        </div>

                        <center>
                            <a href="http://localhost:8080/accedi" class="btn">Accedi Ora</a>
                        </center>
                    </div>
                    <div class="footer">
                        MediBook System - Non rispondere a questa email.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nome, cognome, destinatario, passwordTemp);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email benvenuto inviata a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Errore invio email benvenuto: " + e.getMessage());
        }
    }

    private String costruisciHtmlModifica(String nome, String medico, String data, String ora) {
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

    public void inviaEmailModificaMedico(String destinatarioMedico, String nomePaziente,
                                         String nuovaData, String nuovaOra) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("tuaemail@gmail.com");
            helper.setTo(destinatarioMedico);
            helper.setSubject("📅 Aggiornamento Agenda - MediBook");

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; background-color: #f8f9fa; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; border-top: 5px solid #17a2b8; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .content { padding: 30px; color: #333; }
                    .box-info { background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #007bff; }
                    .footer { text-align: center; padding: 15px; font-size: 12px; color: #777; background: #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="content">
                        <h3>Gentile Dottore,</h3>
                        <p>La segreteria ha modificato un appuntamento nella tua agenda.</p>
                        
                        <div class="box-info">
                            <p>👤 <strong>Paziente:</strong> %s</p>
                            <p>📅 <strong>Nuova Data:</strong> %s</p>
                            <p>🕒 <strong>Nuovo Orario:</strong> %s</p>
                        </div>
                        
                        <p>Puoi consultare i dettagli aggiornati nella tua area riservata.</p>
                    </div>
                    <div class="footer">MediBook System - Notifica Automatica</div>
                </div>
            </body>
            </html>
            """.formatted(nomePaziente, nuovaData, nuovaOra);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email inviata al medico: " + destinatarioMedico);

        } catch (MessagingException e) {
            System.err.println("Errore email medico: " + e.getMessage());
        }
    }
}