package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.OperationRefuseeException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;
    private final IVehiculeRepository vehiculeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        return chargerAgence(id);
    }

    @Override
    public Agence create(Agence agence) {
        agence.setIdAgence(null);
        agence.setEmployes(new ArrayList<>());   // les affectations passent par affecterEmploye
        agence.setVehicules(new ArrayList<>());  // et par affecterVehicule
        return agenceRepository.save(agence);
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existante = chargerAgence(id);
        existante.setNom(agence.getNom());
        existante.setVille(agence.getVille());
        existante.setAdresse(agence.getAdresse());
        existante.setTelephone(agence.getTelephone());
        return existante;   // les listes d'employés et de véhicules ne sont pas écrasées
    }

    @Override
    public void delete(Long id) {
        Agence agence = chargerAgence(id);
        // les employés et les véhicules sont désaffectés, pas supprimés
        new ArrayList<>(agence.getEmployes()).forEach(agence::removeEmploye);
        new ArrayList<>(agence.getVehicules()).forEach(agence::removeVehicule);
        agenceRepository.delete(agence);
    }

    @Override
    public Agence affecterEmploye(Long agenceId, Long employeId) {
        Agence agence = chargerAgence(agenceId);
        Employe employe = chargerEmploye(employeId);
        if (employe.getAgence() != null) {
            throw new OperationRefuseeException("L'employé " + employeId + " est déjà affecté à l'agence "
                    + employe.getAgence().getIdAgence());
        }
        agence.addEmploye(employe);   // UPDATE employe SET agence_id = ? au commit
        return agence;
    }

    @Override
    public void desaffecterEmploye(Long agenceId, Long employeId) {
        Agence agence = chargerAgence(agenceId);
        Employe employe = chargerEmploye(employeId);
        if (employe.getAgence() == null || !employe.getAgence().getIdAgence().equals(agenceId)) {
            throw new OperationRefuseeException(
                    "L'employé " + employeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeEmploye(employe);   // UPDATE employe SET agence_id = NULL ; l'employé est conservé
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> findEmployes(Long agenceId) {
        return List.copyOf(chargerAgence(agenceId).getEmployes());
    }

    @Override
    public Agence affecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = chargerAgence(agenceId);
        Vehicule vehicule = chargerVehicule(vehiculeId);
        if (vehicule.getAgence() != null) {
            throw new OperationRefuseeException("Le véhicule " + vehiculeId + " est déjà affecté à l'agence "
                    + vehicule.getAgence().getIdAgence() + " : utilisez le transfert");
        }
        agence.addVehicule(vehicule);   // UPDATE vehicule SET agence_id = ? au commit
        return agence;
    }

    @Override
    public void desaffecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = chargerAgence(agenceId);
        Vehicule vehicule = chargerVehicule(vehiculeId);
        if (vehicule.getAgence() == null || !vehicule.getAgence().getIdAgence().equals(agenceId)) {
            throw new OperationRefuseeException(
                    "Le véhicule " + vehiculeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeVehicule(vehicule);   // UPDATE vehicule SET agence_id = NULL ; le véhicule est conservé
    }

    @Override
    public Vehicule transfererVehicule(Long vehiculeId, Long agenceCibleId) {
        Vehicule vehicule = chargerVehicule(vehiculeId);
        if (vehicule.getAgence() != null && vehicule.getAgence().getIdAgence().equals(agenceCibleId)) {
            throw new OperationRefuseeException(
                    "Le véhicule " + vehiculeId + " est déjà dans l'agence " + agenceCibleId);
        }
        if (vehicule.getAgence() != null) {
            vehicule.getAgence().removeVehicule(vehicule);   // 1re modification
        }
        Agence cible = chargerAgence(agenceCibleId);          // agence introuvable => rollback
        cible.addVehicule(vehicule);                          // 2e modification
        return vehicule;                                      // un seul UPDATE vehicule SET agence_id au commit
    }

    private Vehicule chargerVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    private Agence chargerAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    private Employe chargerEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }
}
