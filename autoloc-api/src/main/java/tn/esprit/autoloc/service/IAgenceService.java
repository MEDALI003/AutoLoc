package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;

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

    Agence affecterVehicule(Long agenceId, Long vehiculeId);

    void desaffecterVehicule(Long agenceId, Long vehiculeId);

    Vehicule transfererVehicule(Long vehiculeId, Long agenceCibleId);
}
