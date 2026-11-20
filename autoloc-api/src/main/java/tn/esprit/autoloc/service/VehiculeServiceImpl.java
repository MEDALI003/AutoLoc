package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.OperationRefuseeException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule findById(Long id) {
        return chargerVehicule(id);
    }

    @Override
    public Vehicule create(Vehicule vehicule) {
        vehicule.setIdVehicule(null);
        vehicule.setEquipements(new HashSet<>());   // les affectations passent par affecterEquipement
        vehicule.setAgence(null);                   // et par IAgenceService
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = chargerVehicule(id);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        return existant;   // les équipements et l'agence ne sont pas écrasés
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = chargerVehicule(id);
        if (vehicule.getAgence() != null) {
            vehicule.getAgence().removeVehicule(vehicule);
        }
        vehiculeRepository.delete(vehicule);   // les lignes vehicule_equipement suivent
    }

    @Override
    public Vehicule affecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = chargerVehicule(vehiculeId);
        Equipement equipement = chargerEquipement(equipementId);
        if (vehicule.getEquipements().contains(equipement)) {
            throw new OperationRefuseeException(
                    "L'équipement " + equipementId + " est déjà affecté au véhicule " + vehiculeId);
        }
        vehicule.addEquipement(equipement);   // INSERT INTO vehicule_equipement au commit
        return vehicule;
    }

    @Override
    public void desaffecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = chargerVehicule(vehiculeId);
        Equipement equipement = chargerEquipement(equipementId);
        if (!vehicule.getEquipements().contains(equipement)) {
            throw new OperationRefuseeException(
                    "L'équipement " + equipementId + " n'est pas affecté au véhicule " + vehiculeId);
        }
        vehicule.removeEquipement(equipement);   // DELETE FROM vehicule_equipement ; équipement conservé
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> findEquipements(Long vehiculeId) {
        return List.copyOf(chargerVehicule(vehiculeId).getEquipements());
    }

    private Vehicule chargerVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    private Equipement chargerEquipement(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }
}
