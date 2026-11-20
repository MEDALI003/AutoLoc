package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

public interface IContratService {

    List<Contrat> findAll();

    Contrat findById(Long id);

    Contrat update(Long id, Contrat contrat);

    void delete(Long id);

    Contrat creerContrat(Long reservationId, Contrat contrat);

    Paiement ajouterPaiement(Long contratId, Paiement paiement);

    void retirerPaiement(Long contratId, Long paiementId);

    List<Paiement> findPaiements(Long contratId);
}
