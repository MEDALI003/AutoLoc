package tn.esprit.autoloc.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions métier en réponses HTTP (format RFC 9457 « problem detail »).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return probleme(HttpStatus.NOT_FOUND, "Ressource introuvable", ex.getMessage());
    }

    @ExceptionHandler(OperationRefuseeException.class)
    public ProblemDetail handleOperationRefusee(OperationRefuseeException ex) {
        return probleme(HttpStatus.CONFLICT, "Opération refusée", ex.getMessage());
    }

    @ExceptionHandler(DonneeInvalideException.class)
    public ProblemDetail handleDonneeInvalide(DonneeInvalideException ex) {
        return probleme(HttpStatus.BAD_REQUEST, "Donnée invalide", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegrite(DataIntegrityViolationException ex) {
        return probleme(HttpStatus.CONFLICT, "Contrainte d'intégrité violée",
                "L'opération viole une contrainte de la base de données");
    }

    private static ProblemDetail probleme(HttpStatus statut, String titre, String detail) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, detail);
        probleme.setTitle(titre);
        return probleme;
    }
}
