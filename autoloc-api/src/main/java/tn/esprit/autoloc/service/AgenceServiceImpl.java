package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.OperationRefuseeException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;

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
        return agenceRepository.save(agence);
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existante = chargerAgence(id);
        existante.setNom(agence.getNom());
        existante.setVille(agence.getVille());
        existante.setAdresse(agence.getAdresse());
        existante.setTelephone(agence.getTelephone());
        return existante;   // la liste des employés n'est pas écrasée
    }

    @Override
    public void delete(Long id) {
        Agence agence = chargerAgence(id);
        // les employés sont désaffectés, pas supprimés
        new ArrayList<>(agence.getEmployes()).forEach(agence::removeEmploye);
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

    private Agence chargerAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    private Employe chargerEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }
}
