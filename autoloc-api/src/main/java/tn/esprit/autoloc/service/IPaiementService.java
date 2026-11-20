package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

/**
 * Lecture seule : un paiement se crée et se retire à travers son contrat (IContratService).
 */
public interface IPaiementService {

    List<Paiement> findAll();

    Paiement findById(Long id);
}
