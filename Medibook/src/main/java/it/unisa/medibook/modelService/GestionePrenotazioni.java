package it.unisa.medibook.modelService;

import it.unisa.medibook.model.Medico;
import it.unisa.medibook.model.Paziente;
import it.unisa.medibook.model.Prenotazione;
import it.unisa.medibook.modelStorage.MedicoRepository;
import it.unisa.medibook.modelStorage.PazienteRepository;
import it.unisa.medibook.modelStorage.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GestionePrenotazioni {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PazienteRepository pazienteRepository;

    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra, String nuovoStato) throws Exception {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();

            // 1. Se la data o l'ora sono diverse da quelle attuali, facciamo i controlli
            if (!p.getData().equals(nuovaData) || !p.getOra().equals(nuovaOra)) {

                if (nuovaData.isBefore(LocalDate.now())) {
                    throw new Exception("Errore: Non è possibile spostare una visita nel passato.");
                }

                // Controlliamo se il nuovo slot è occupato (escludendo se stessa)
                boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(
                        p.getMedico().getId(),
                        nuovaData,
                        nuovaOra,
                        id
                );

                if (slotOccupato) {
                    throw new Exception("Errore: Orario non disponibile. Il medico ha già una visita in questo orario.");
                }

                // Applichiamo i nuovi orari
                p.setData(nuovaData);
                p.setOra(nuovaOra);
            }

            // 2. Aggiorniamo lo stato
            p.setStato(nuovoStato);

            return prenotazioneRepository.save(p);
        }
        throw new Exception("Prenotazione non trovata");
    }

    public Prenotazione aggiornaStatoVisita(Integer id, String nuovoStato) {
        Optional<Prenotazione> pOpt = prenotazioneRepository.findById(id);

        if (pOpt.isPresent()) {
            Prenotazione p = pOpt.get();
            p.setStato(nuovoStato);
            return prenotazioneRepository.save(p);
        }
        return null;
    }

    public List<Prenotazione> visualizzaVisiteMedico(Integer medicoId) {
        return prenotazioneRepository.findByMedicoId(medicoId);
    }

    public List<Prenotazione> visualizzaVisitePaziente(Integer pazienteId) {
        return prenotazioneRepository.findByPazienteId(pazienteId);
    }

    public Prenotazione nuovaPrenotazione(Integer pazienteId, Integer medicoId, LocalDate data, LocalTime ora) throws Exception {

        if (data.isBefore(LocalDate.now())) {
            throw new Exception("Errore: Non puoi prenotare nel passato!");
        }

        boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOra(medicoId, data, ora);

        if (slotOccupato) {
            throw new Exception("Errore: Orario non disponibile per questo medico.");
        }

        Optional<Paziente> pazienteOpt = pazienteRepository.findById(pazienteId);
        Optional<Medico> medicoOpt = medicoRepository.findById(Long.valueOf(medicoId));

        if (pazienteOpt.isPresent() && medicoOpt.isPresent()) {
            Prenotazione p = new Prenotazione();
            p.setData(data);
            p.setOra(ora);
            p.setStato("PRENOTATA");
            p.setPaziente(pazienteOpt.get());
            p.setMedico(medicoOpt.get());

            return prenotazioneRepository.save(p);
        } else {
            throw new Exception("Errore: Utente o Medico non trovato.");
        }
    }


    public List<Prenotazione> visualizzaVisitePerCalendario(Integer medicoId) {
        // Recuperiamo solo quelle con stato "PRENOTATA"
        // (Ignoriamo quelle CANCELLATE o già CONCLUSE per non affollare il calendario)
        return prenotazioneRepository.findByMedicoIdAndStato(medicoId, "PRENOTATA");
    }
    public List<Integer> getGiorniLavorativi(Integer medicoId) {
        Medico m = medicoRepository.findById(Long.valueOf(medicoId)).orElse(null);
        if (m == null || m.getTurni() == null) return Collections.emptyList();

        List<Integer> giorni = new ArrayList<>();

        // Esempio stringa: "1:09:00-13:00,3:15:00-19:00"
        String[] regole = m.getTurni().split(",");

        for (String regola : regole) {
            // regola = "1:09:00-13:00"
            String[] parti = regola.split(":");
            int giorno = Integer.parseInt(parti[0]); // Prende "1"
            giorni.add(giorno);
        }
        return giorni;
    }

    // 2. API SLOT LIBERI (Calcola orari in base al giorno specifico)
// 2. API SLOT LIBERI (Calcola orari in base al giorno specifico)
    public List<LocalTime> getOrariLiberi(Integer medicoId, LocalDate data) {
        System.out.println("--- API ORARI RICHIESTA: Medico " + medicoId + " - Data " + data + " ---");

        // 1. Recupero Medico
        Medico m = medicoRepository.findById(Long.valueOf(medicoId)).orElse(null);
        if (m == null || m.getTurni() == null) {
            System.out.println("ERRORE: Medico nullo o turni nulli.");
            return Collections.emptyList();
        }

        // 2. Calcolo giorno della settimana (1=Lun, 7=Dom)
        int giornoRichiesto = data.getDayOfWeek().getValue();
        System.out.println("Giorno della settimana richiesto: " + giornoRichiesto);

        LocalTime inizioTurno = null;
        LocalTime fineTurno = null;

        // 3. Parsing della stringa (es. "1:09:00-13:00, 3:15:00-19:00" oppure "1:9-13")
        try {
            // Rimuovo spazi bianchi
            String turniPuliti = m.getTurni().replace(" ", "");
            String[] regole = turniPuliti.split(",");

            for (String regola : regole) {
                // Controllo sicurezza formato
                if (!regola.contains(":")) continue;

                // Divide il giorno dagli orari. Es: "1" e "9-13"
                String[] parti = regola.split(":", 2); // Split limitato a 2 per evitare errori se l'ora ha i due punti
                int giornoRegola = Integer.parseInt(parti[0]);

                // Se abbiamo trovato il giorno che ci interessa
                if (giornoRegola == giornoRichiesto) {
                    System.out.println("Trovata regola per oggi: " + regola);

                    String[] orari = parti[1].split("-"); // ["9", "13"] oppure ["09:00", "13:00"]

                    // *** FIX: Normalizzazione orario (aggiunge :00 se manca) ***
                    String startStr = orari[0];
                    String endStr = orari[1];

                    if (!startStr.contains(":")) startStr += ":00";
                    if (!endStr.contains(":")) endStr += ":00";

                    // Formatta anche i numeri singoli (es. "9:00" -> "09:00") per sicurezza, anche se parse lo gestisce spesso
                    if (startStr.length() == 4) startStr = "0" + startStr;
                    if (endStr.length() == 4) endStr = "0" + endStr;

                    inizioTurno = LocalTime.parse(startStr);
                    fineTurno = LocalTime.parse(endStr);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("CRASH DURANTE IL PARSING TURNI: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        // Se non abbiamo trovato orari per oggi
        if (inizioTurno == null) {
            System.out.println("Nessun turno trovato per questo giorno.");
            return Collections.emptyList();
        }

        // 4. Generazione Slot Temporali
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = inizioTurno;

        while (current.isBefore(fineTurno)) {
            slots.add(current);
            current = current.plusMinutes(30);
        }

        // 5. Rimozione Slot Occupati
        List<Prenotazione> occupati = prenotazioneRepository.findByMedicoIdAndData(medicoId, data);
        System.out.println("Prenotazioni già esistenti oggi: " + occupati.size());

        for (Prenotazione p : occupati) {
            slots.remove(p.getOra());
        }

        System.out.println("Slot restituiti al frontend: " + slots.size());
        return slots;
    }
}