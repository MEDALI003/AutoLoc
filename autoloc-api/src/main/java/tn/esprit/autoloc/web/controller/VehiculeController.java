package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "CRUD des véhicules et affectation des équipements (ManyToMany)")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @GetMapping
    @Operation(summary = "Lister les véhicules")
    public List<Vehicule> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire un véhicule (avec ses équipements)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public Vehicule findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un véhicule")
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les champs d'un véhicule (les liens sont conservés)")
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public void delete(@PathVariable Long id) {
        vehiculeService.delete(id);
    }

    @PostMapping("/{id}/equipements/{equipementId}")
    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté ; renvoie le véhicule"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable"),
            @ApiResponse(responseCode = "409", description = "Équipement déjà affecté à ce véhicule")})
    public Vehicule affecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        return vehiculeService.affecterEquipement(id, equipementId);
    }

    @DeleteMapping("/{id}/equipements/{equipementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirer un équipement d'un véhicule (l'équipement est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement retiré"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable"),
            @ApiResponse(responseCode = "409", description = "Équipement non affecté à ce véhicule")})
    public void desaffecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        vehiculeService.desaffecterEquipement(id, equipementId);
    }

    @GetMapping("/{id}/equipements")
    @Operation(summary = "Lister les équipements d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des équipements"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public List<Equipement> findEquipements(@PathVariable Long id) {
        return vehiculeService.findEquipements(id);
    }
}
