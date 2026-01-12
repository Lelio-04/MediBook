package it.unisa.medibook.service;

import it.unisa.medibook.model.*;
import it.unisa.medibook.modelStorage.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GestionePrenotazioni {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PazienteRepository pazienteRepository;

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private EmailService emailService;


    public Prenotazione getPrenotazioneById(Integer id) {
        return prenotazioneRepository.findById(id).orElse(null);
    }

    public List<Prenotazione> visualizzaVisiteMedico(Integer medicoId) {
        return prenotazioneRepository.findByMedicoId(medicoId);
    }



    public List<Prenotazione> getVisiteFuture(Integer pazienteId) {
        return prenotazioneRepository.findByPazienteId(pazienteId).stream()
                .filter(v -> "PRENOTATA".equals(v.getStato()))
                .collect(Collectors.toList());
    }

    public List<Prenotazione> getVisiteStorico(Integer pazienteId) {
        return prenotazioneRepository.findByPazienteId(pazienteId).stream()
                .filter(v -> "EFFETTUATA".equals(v.getStato()) ||
                        "CONCLUSA".equals(v.getStato()) ||
                        "ANNULLATA".equals(v.getStato()))
                .collect(Collectors.toList());
    }


    @Transactional
    public Prenotazione nuovaPrenotazione(Integer pazienteId, Integer medicoId, LocalDate data, LocalTime ora) throws Exception {
        if (data.isBefore(LocalDate.now())) {
            throw new Exception("Errore: Non puoi prenotare nel passato!");
        }

        boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOra(medicoId, data, ora);
        if (slotOccupato) {
            throw new Exception("Errore: Orario non disponibile per questo medico.");
        }

        Paziente paziente = pazienteRepository.findById(pazienteId)
                .orElseThrow(() -> new Exception("Paziente non trovato"));
        Medico medico = medicoRepository.findById(Long.valueOf(medicoId))
                .orElseThrow(() -> new Exception("Medico non trovato"));

        Prenotazione p = new Prenotazione();
        p.setData(data);
        p.setOra(ora);
        p.setStato("PRENOTATA");
        p.setPaziente(paziente);
        p.setMedico(medico);

        return prenotazioneRepository.save(p);
    }

    @Transactional
    public Prenotazione modificaPrenotazione(Integer id, LocalDate nuovaData, LocalTime nuovaOra, String nuovoStato) throws Exception {
        Prenotazione p = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new Exception("Prenotazione non trovata"));

        if (!p.getData().equals(nuovaData) || !p.getOra().equals(nuovaOra)) {
            if (nuovaData.isBefore(LocalDate.now())) {
                throw new Exception("Errore: Non è possibile spostare una visita nel passato.");
            }

            boolean slotOccupato = prenotazioneRepository.existsByMedicoIdAndDataAndOraAndIdNot(
                    p.getMedico().getId(), nuovaData, nuovaOra, id);

            if (slotOccupato) {
                throw new Exception("Errore: Orario non disponibile.");
            }

            p.setData(nuovaData);
            p.setOra(nuovaOra);
        }

        p.setStato(nuovoStato);
        return prenotazioneRepository.save(p);
    }

    @Transactional
    public void aggiornaStatoVisita(Integer id, String nuovoStato) {
        prenotazioneRepository.findById(id).ifPresent(p -> {
            // --- FIX: Aggiungi questo controllo di sicurezza ---
            if ("CONCLUSA".equals(p.getStato()) || "ANNULLATA".equals(p.getStato())) {
                // Se la visita è già chiusa, non facciamo nulla (o lanciamo un'eccezione)
                return;
            }

            p.setStato(nuovoStato);
            prenotazioneRepository.save(p);
        });
    }


    public List<Prenotazione> visualizzaVisitePerCalendario(Integer medicoId) {
        return prenotazioneRepository.findByMedicoIdAndStato(medicoId, "PRENOTATA");
    }

    public List<Map<String, Object>> getEventiCalendarioJSON(Integer medicoId) {
        List<Prenotazione> visite = visualizzaVisitePerCalendario(medicoId);
        List<Map<String, Object>> eventi = new ArrayList<>();

        for (Prenotazione p : visite) {
            Map<String, Object> evento = new HashMap<>();
            evento.put("id", p.getId());
            evento.put("title", p.getPaziente().getCognome() + " " + p.getPaziente().getNome());
            evento.put("start", p.getData().toString() + "T" + p.getOra().toString());
            evento.put("backgroundColor", "#28a745");
            evento.put("extendedProps", Map.of(
                    "stato", p.getStato(),
                    "codiceFiscale", p.getPaziente().getCodiceFiscale()
            ));
            eventi.add(evento);
        }
        return eventi;
    }

    public List<Integer> getGiorniLavorativi(Integer medicoId) {
        Medico m = medicoRepository.findById(Long.valueOf(medicoId)).orElse(null);
        if (m == null || m.getTurni() == null) return Collections.emptyList();

        List<Integer> giorni = new ArrayList<>();
        String[] regole = m.getTurni().split(",");
        for (String regola : regole) {
            String[] parti = regola.split(":");
            giorni.add(Integer.parseInt(parti[0]));
        }
        return giorni;
    }

    public List<LocalTime> getOrariLiberi(Integer medicoId, LocalDate data) {
        Medico m = medicoRepository.findById(Long.valueOf(medicoId)).orElse(null);
        if (m == null || m.getTurni() == null) return Collections.emptyList();

        int giornoRichiesto = data.getDayOfWeek().getValue();
        LocalTime inizioTurno = null;
        LocalTime fineTurno = null;

        try {
            String[] regole = m.getTurni().replace(" ", "").split(",");
            for (String regola : regole) {
                if (!regola.contains(":")) continue;
                String[] parti = regola.split(":", 2);
                if (Integer.parseInt(parti[0]) == giornoRichiesto) {
                    String[] orari = parti[1].split("-");
                    String startStr = orari[0].contains(":") ? orari[0] : orari[0] + ":00";
                    String endStr = orari[1].contains(":") ? orari[1] : orari[1] + ":00";
                    inizioTurno = LocalTime.parse(startStr.length() == 4 ? "0" + startStr : startStr);
                    fineTurno = LocalTime.parse(endStr.length() == 4 ? "0" + endStr : endStr);
                    break;
                }
            }
        } catch (Exception e) { return Collections.emptyList(); }

        if (inizioTurno == null) return Collections.emptyList();

        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = inizioTurno;
        while (current.isBefore(fineTurno)) {
            slots.add(current);
            current = current.plusMinutes(30);
        }

        List<Prenotazione> occupati = prenotazioneRepository.findByMedicoIdAndData(medicoId, data);
        for (Prenotazione p : occupati) {
            slots.remove(p.getOra());
        }
        return slots;
    }

    // --- RECENSIONI ---

    @Transactional
    public void salvaRecensione(Long idPrenotazione, int voto, String commento, Paziente autore) throws Exception {
        Prenotazione p = prenotazioneRepository.findById(Math.toIntExact(idPrenotazione))
                .orElseThrow(() -> new Exception("Prenotazione non trovata"));

        if (p.getRecensione() != null) {
            throw new Exception("Hai già recensito questa visita!");
        }

        Recensione r = new Recensione(voto, commento, p.getMedico(), autore);
        r.setPrenotazione(p);
        recensioneRepository.save(r);
    }
    public List<Prenotazione> ricercaPrenotazioni(String query, String filtro) {
        if ("oggi".equals(filtro)) {
            return prenotazioneRepository.findByData(LocalDate.now());
        }
        if (query != null && !query.trim().isEmpty()) {
            return prenotazioneRepository.findByPazienteNomeContainingIgnoreCaseOrPazienteCognomeContainingIgnoreCase(query, query);
        }
        return prenotazioneRepository.findAll();
    }
    public void inviaNotificheModifica(Integer id) {
        Prenotazione p = prenotazioneRepository.findById(id).orElse(null);
        if (p != null && p.getPaziente().getEmail() != null) {
            String dataFormat = p.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String oraFormat = p.getOra().toString();
            String nomePaziente = p.getPaziente().getNome() + " " + p.getPaziente().getCognome();
            String nomeMedico = p.getMedico().getCognome();

            try {
                String emailPaziente = p.getPaziente().getEmail();
                if (emailPaziente != null && !emailPaziente.isEmpty()) {
                    String oggetto = "⚠️ Modifica Appuntamento - MediBook";

                    emailService.inviaEmailModifica(
                            emailPaziente,
                            oggetto,
                            nomePaziente,
                            nomeMedico,
                            dataFormat,
                            oraFormat
                    );
                }
            } catch (Exception e) {
                System.err.println("Errore invio email paziente: " + e.getMessage());
            }


            try {
                String emailMedico = p.getMedico().getEmail();
                // Controlliamo che il medico abbia una mail
                if (emailMedico != null && !emailMedico.isEmpty()) {

                    emailService.inviaEmailModificaMedico(
                            emailMedico,
                            nomePaziente,
                            dataFormat,
                            oraFormat
                    );
                }
            } catch (Exception e) {
                System.err.println("Errore invio email medico: " + e.getMessage());
            }
        }
    }
}