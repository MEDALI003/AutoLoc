package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employe findById(Long id) {
        return chargerEmploye(id);
    }

    @Override
    public Employe create(Employe employe) {
        employe.setIdEmploye(null);
        employe.setAgence(null);   // l'affectation passe par IAgenceService
        return employeRepository.save(employe);
    }

    @Override
    public Employe update(Long id, Employe employe) {
        Employe existant = chargerEmploye(id);
        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());
        return existant;   // l'agence n'est pas modifiée
    }

    @Override
    public void delete(Long id) {
        Employe employe = chargerEmploye(id);
        if (employe.getAgence() != null) {
            employe.getAgence().removeEmploye(employe);
        }
        employeRepository.delete(employe);
    }

    private Employe chargerEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }
}
