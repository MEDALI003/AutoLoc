package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Equipement findById(Long id) {
        return chargerEquipement(id);
    }

    @Override
    public Equipement create(Equipement equipement) {
        equipement.setIdEquipement(null);
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement update(Long id, Equipement equipement) {
        Equipement existant = chargerEquipement(id);
        existant.setLibelle(equipement.getLibelle());
        return existant;
    }

    @Override
    public void delete(Long id) {
        Equipement equipement = chargerEquipement(id);
        // Vehicule possède la table de jointure : on retire d'abord les lignes vehicule_equipement
        for (Vehicule vehicule : new ArrayList<>(equipement.getVehicules())) {
            vehicule.removeEquipement(equipement);
        }
        equipementRepository.delete(equipement);
    }

    private Equipement chargerEquipement(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }
}
