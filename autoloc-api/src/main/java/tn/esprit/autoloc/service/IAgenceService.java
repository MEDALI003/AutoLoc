package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public interface IAgenceService {

    List<Agence> findAll();

    Agence findById(Long id);

    Agence create(Agence agence);

    Agence update(Long id, Agence agence);

    void delete(Long id);

    Agence affecterEmploye(Long agenceId, Long employeId);

    void desaffecterEmploye(Long agenceId, Long employeId);

    List<Employe> findEmployes(Long agenceId);
}
