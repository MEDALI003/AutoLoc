package tn.esprit.autoloc.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Insere quelques vehicules de demonstration au demarrage.
 * Actif uniquement si autoloc.demo-data.enabled=true (profil "dev").
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "autoloc.demo-data.enabled", havingValue = "true")
public class DemoDataLoader implements CommandLineRunner {

    private final VehiculeRepository vehiculeRepository;

    @Override
    public void run(String... args) {
        if (vehiculeRepository.count() > 0) {
            log.info("Donnees de demonstration deja presentes, insertion ignoree");
            return;
        }
        List<Vehicule> vehicules = List.of(
                new Vehicule(null, "123 TU 4567", "Peugeot", "208",
                        CategorieVehicule.CITADINE, new BigDecimal("90.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "201 TU 1122", "Volkswagen", "Passat",
                        CategorieVehicule.BERLINE, new BigDecimal("150.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "215 TU 9876", "Hyundai", "Tucson",
                        CategorieVehicule.SUV, new BigDecimal("180.00"), StatutVehicule.MAINTENANCE)
        );
        vehiculeRepository.saveAll(vehicules);
        log.info("{} vehicules de demonstration inseres", vehicules.size());
    }
}
