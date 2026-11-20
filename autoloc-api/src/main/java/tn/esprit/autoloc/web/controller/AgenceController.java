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
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
@Tag(name = "Agences", description = "CRUD des agences et affectation des employés")
public class AgenceController {

    private final IAgenceService agenceService;

    @GetMapping
    @Operation(summary = "Lister les agences")
    public List<Agence> findAll() {
        return agenceService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire une agence (avec ses employés)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")})
    public Agence findById(@PathVariable Long id) {
        return agenceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une agence")
    public Agence create(@RequestBody Agence agence) {
        return agenceService.create(agence);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les champs d'une agence (les liens sont conservés)")
    public Agence update(@PathVariable Long id, @RequestBody Agence agence) {
        return agenceService.update(id, agence);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une agence (ses employés sont désaffectés, pas supprimés)")
    public void delete(@PathVariable Long id) {
        agenceService.delete(id);
    }

    @PostMapping("/{agenceId}/employes/{employeId}")
    @Operation(summary = "Affecter un employé à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé affecté ; renvoie l'agence mise à jour"),
            @ApiResponse(responseCode = "404", description = "Agence ou employé introuvable"),
            @ApiResponse(responseCode = "409", description = "Employé déjà affecté (à cette agence ou à une autre)")})
    public Agence affecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        return agenceService.affecterEmploye(agenceId, employeId);
    }

    @DeleteMapping("/{agenceId}/employes/{employeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désaffecter un employé de son agence (l'employé est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé désaffecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou employé introuvable"),
            @ApiResponse(responseCode = "409", description = "L'employé n'est pas affecté à cette agence")})
    public void desaffecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        agenceService.desaffecterEmploye(agenceId, employeId);
    }

    @GetMapping("/{agenceId}/employes")
    @Operation(summary = "Lister les employés d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des employés"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")})
    public List<Employe> findEmployes(@PathVariable Long agenceId) {
        return agenceService.findEmployes(agenceId);
    }

    @PostMapping("/{agenceId}/vehicules/{vehiculeId}")
    @Operation(summary = "Affecter un véhicule (sans agence) à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté ; renvoie l'agence mise à jour"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Véhicule déjà affecté à une agence")})
    public Agence affecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        return agenceService.affecterVehicule(agenceId, vehiculeId);
    }

    @DeleteMapping("/{agenceId}/vehicules/{vehiculeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désaffecter un véhicule de son agence (le véhicule est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule désaffecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Le véhicule n'est pas affecté à cette agence")})
    public void desaffecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        agenceService.desaffecterVehicule(agenceId, vehiculeId);
    }

    @PutMapping("/{agenceCibleId}/vehicules/{vehiculeId}")
    @Operation(summary = "Transférer un véhicule vers une autre agence (met à jour vehicule.agence_id)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule transféré"),
            @ApiResponse(responseCode = "404", description = "Agence cible ou véhicule introuvable (rollback)"),
            @ApiResponse(responseCode = "409", description = "Le véhicule est déjà dans l'agence cible")})
    public Vehicule transfererVehicule(@PathVariable Long agenceCibleId, @PathVariable Long vehiculeId) {
        return agenceService.transfererVehicule(vehiculeId, agenceCibleId);
    }
}
