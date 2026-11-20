package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return chargerReservation(id);
    }

    @Override
    public Reservation create(Reservation reservation) {
        reservation.setIdReservation(null);
        reservation.setContrat(null);   // le contrat s'établit via POST /api/reservations/{id}/contrat
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        Reservation existante = chargerReservation(id);
        existante.setDateDebut(reservation.getDateDebut());
        existante.setDateFin(reservation.getDateFin());
        existante.setStatut(reservation.getStatut());
        return existante;   // le contrat n'est pas écrasé
    }

    @Override
    public void delete(Long id) {
        reservationRepository.delete(chargerReservation(id));   // cascade : contrat puis paiements
    }

    private Reservation chargerReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }
}
