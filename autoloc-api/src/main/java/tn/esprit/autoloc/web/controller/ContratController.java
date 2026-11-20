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
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Contrats et paiements (composition : cascade + orphanRemoval)")
public class ContratController {

    private final IContratService contratService;

    @GetMapping
    @Operation(summary = "Lister les contrats")
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire un contrat (avec ses paiements)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public Contrat findById(@PathVariable Long id) {
        return contratService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les champs d'un contrat (les paiements ne sont pas modifiés)")
    public Contrat update(@PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un contrat et, par cascade, tous ses paiements")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat et paiements supprimés"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public void delete(@PathVariable Long id) {
        contratService.delete(id);
    }

    @PostMapping("/{id}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter un paiement à un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement ajouté"),
            @ApiResponse(responseCode = "400", description = "Paiement invalide (montant ≤ 0)"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public Paiement ajouterPaiement(@PathVariable Long id, @RequestBody Paiement paiement) {
        return contratService.ajouterPaiement(id, paiement);
    }

    @DeleteMapping("/{id}/paiements/{paiementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirer un paiement d'un contrat : il est supprimé en base (orphanRemoval)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable"),
            @ApiResponse(responseCode = "409", description = "Le paiement appartient à un autre contrat")})
    public void retirerPaiement(@PathVariable Long id, @PathVariable Long paiementId) {
        contratService.retirerPaiement(id, paiementId);
    }

    @GetMapping("/{id}/paiements")
    @Operation(summary = "Lister les paiements d'un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des paiements"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public List<Paiement> findPaiements(@PathVariable Long id) {
        return contratService.findPaiements(id);
    }
}
