package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.DonneeInvalideException;
import tn.esprit.autoloc.exception.OperationRefuseeException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final IPaiementRepository paiementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return chargerContrat(id);
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = chargerContrat(id);
        // seuls les champs simples : remplacer la liste (orphanRemoval) supprimerait les paiements
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return existant;
    }

    @Override
    public void delete(Long id) {
        Contrat contrat = chargerContrat(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().retirerContrat();
        }
        contratRepository.delete(contrat);   // cascade : DELETE des paiements puis du contrat
    }

    @Override
    public Contrat creerContrat(Long reservationId, Contrat contrat) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        if (reservation.getContrat() != null) {
            throw new OperationRefuseeException("La réservation " + reservationId + " a déjà un contrat");
        }

        // paiements reçus dans le JSON : détachés puis validés et rattachés un à un
        List<Paiement> paiements = new ArrayList<>(contrat.getPaiements());
        paiements.forEach(contrat::removePaiement);

        contrat.setIdContrat(null);
        reservation.etablirContrat(contrat);
        Contrat enregistre = contratRepository.save(contrat);   // INSERT contrat

        // un paiement invalide lève une RuntimeException : rollback complet, contrat compris
        for (Paiement paiement : paiements) {
            verifierPaiement(paiement);
            enregistre.addPaiement(paiement);   // INSERT paiement au commit (cascade)
        }
        return enregistre;
    }

    @Override
    public Paiement ajouterPaiement(Long contratId, Paiement paiement) {
        Contrat contrat = chargerContrat(contratId);
        verifierPaiement(paiement);
        contrat.addPaiement(paiement);
        return paiement;
    }

    @Override
    public void retirerPaiement(Long contratId, Long paiementId) {
        Contrat contrat = chargerContrat(contratId);
        Paiement paiement = contrat.getPaiements().stream()
                .filter(p -> Objects.equals(p.getIdPaiement(), paiementId))
                .findFirst()
                .orElseThrow(() -> paiementRepository.existsById(paiementId)
                        ? new OperationRefuseeException(
                                "Le paiement " + paiementId + " n'appartient pas au contrat " + contratId)
                        : new ResourceNotFoundException("Paiement", paiementId));
        contrat.removePaiement(paiement);   // orphanRemoval : DELETE FROM paiement au commit
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paiement> findPaiements(Long contratId) {
        return List.copyOf(chargerContrat(contratId).getPaiements());
    }

    private Contrat chargerContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    private static void verifierPaiement(Paiement paiement) {
        if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
            throw new DonneeInvalideException("Paiement invalide : le montant doit être strictement positif");
        }
        paiement.setIdPaiement(null);
    }
}
