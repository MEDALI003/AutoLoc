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
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Réservations et établissement du contrat")
public class ReservationController {

    private final IReservationService reservationService;
    private final IContratService contratService;

    @GetMapping
    @Operation(summary = "Lister les réservations")
    public List<Reservation> findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire une réservation (avec son contrat)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")})
    public Reservation findById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une réservation")
    public Reservation create(@RequestBody Reservation reservation) {
        return reservationService.create(reservation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les dates et le statut d'une réservation")
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return reservationService.update(id, reservation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une réservation (et, par cascade, son contrat et ses paiements)")
    public void delete(@PathVariable Long id) {
        reservationService.delete(id);
    }

    @PostMapping("/{id}/contrat")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Établir le contrat d'une réservation, avec ses paiements, en un seul appel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat et paiements créés"),
            @ApiResponse(responseCode = "400", description = "Paiement invalide : rollback complet"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "La réservation a déjà un contrat")})
    public Contrat creerContrat(@PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.creerContrat(id, contrat);
    }
}
