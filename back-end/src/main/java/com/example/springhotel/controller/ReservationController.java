package com.example.springhotel.controller;

import com.example.springhotel.dto.ReservationRequestDTO;
import com.example.springhotel.dto.ReservationResponseDTO;
import com.example.springhotel.entity.Reservation;
import com.example.springhotel.repository.ReservationRepository;
import com.example.springhotel.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    /**
     * POST /api/reservations - Créer une nouvelle réservation
     */
    @PostMapping
    public ResponseEntity<?> creerReservation(
            @RequestBody ReservationRequestDTO request,
            Authentication authentication
    ) {
        try {
            String userEmail = authentication != null ? authentication.getName() : null;
            ReservationResponseDTO reservation = reservationService.creerReservation(request, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/code/{code} - Retrouver une réservation par code de confirmation
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getReservationByCode(@PathVariable String code) {
        try {
            Reservation reservation = reservationRepository.findByCodeConfirmation(code);
            if (reservation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Aucune réservation trouvée avec ce code : " + code));
            }
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/user/{userId} - Récupérer toutes les réservations d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUserId(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationRepository.findByUserIdOrderByDateDebutDesc(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/{id} - Récupérer une réservation par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            return reservationRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/reservations/{id}/annuler - Annuler une réservation
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerReservation(@PathVariable Long id) {
        try {
            return reservationRepository.findById(id)
                    .map(reservation -> {
                        reservation.setStatut(Reservation.StatutReservation.ANNULEE);
                        Reservation updated = reservationRepository.save(reservation);
                        return ResponseEntity.ok(updated);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /api/reservations/{id} - Supprimer une réservation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerReservation(@PathVariable Long id) {
        try {
            return reservationRepository.findById(id)
                    .map(reservation -> {
                        reservationRepository.delete(reservation);
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Réservation supprimée avec succès");
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/reservations - Récupérer toutes les réservations (Admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Classe interne pour les réponses d'erreur
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}