package tn.esprit.autoloc.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    public void run(String... args) {
        if (vehiculeRepository.count() > 0) {
            return;
        }
        List<Vehicule> vehicules = List.of(
                new Vehicule(null, "123 TU 4567", "Peugeot", "208", CategorieVehicule.CITADINE,
                        new BigDecimal("90.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "234 TU 5678", "Volkswagen", "Passat", CategorieVehicule.BERLINE,
                        new BigDecimal("150.00"), StatutVehicule.DISPONIBLE),
                new Vehicule(null, "345 TU 6789", "Kia", "Sportage", CategorieVehicule.SUV,
                        new BigDecimal("180.00"), StatutVehicule.MAINTENANCE));
        vehiculeRepository.saveAll(vehicules);
        log.info("{} véhicules de démonstration insérés", vehicules.size());
    }
}
