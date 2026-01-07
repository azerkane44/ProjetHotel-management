package com.example.springhotel.controller;

import com.example.springhotel.dto.ReservationRequestDTO;
import com.example.springhotel.dto.ReservationResponseDTO;
import com.example.springhotel.entity.Chambre;
import com.example.springhotel.entity.Reservation;
import com.example.springhotel.entity.User;
import com.example.springhotel.repository.ChambreRepository;
import com.example.springhotel.repository.ReservationRepository;
import com.example.springhotel.repository.UserRepository;
import com.example.springhotel.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * ✅ POST /api/reservations - Créer une nouvelle réservation (AUTHENTIFICATION REQUISE)
     */
    @PostMapping
    public ResponseEntity<?> creerReservation(
            @RequestBody ReservationRequestDTO request,
            Authentication authentication
    ) {
        System.out.println("📥 Tentative de réservation");
        System.out.println("🔐 Authentication: " + (authentication != null ? authentication.getName() : "null"));
        System.out.println("🔐 Is Authenticated: " + (authentication != null ? authentication.isAuthenticated() : "false"));

        // ✅ VÉRIFICATION : L'utilisateur doit être connecté
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("❌ Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Veuillez vous connecter pour réserver"));
        }

        try {
            // Récupérer l'utilisateur connecté via le token JWT
            String userEmail = authentication.getName();
            System.out.println("📧 Email extrait du token: " + userEmail);

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            System.out.println("✅ Utilisateur connecté : " + user.getEmail() + " (ID: " + user.getId() + ")");

            // Vérifier que la chambre existe
            Chambre chambre = chambreRepository.findById(request.getChambreId())
                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée"));

            System.out.println("🏨 Chambre trouvée : " + chambre.getNom() + " (ID: " + chambre.getId() + ")");

            // Vérifier la disponibilité
            boolean isOccupied = reservationRepository.existsReservationChevauchante(
                    request.getChambreId(),
                    request.getDateDebut(),
                    request.getDateFin()
            );

            if (isOccupied) {
                System.out.println("❌ Chambre non disponible pour ces dates");
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Cette chambre n'est pas disponible pour ces dates"));
            }

            // Calculer le prix total
            long nombreNuits = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin());
            if (nombreNuits <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La date de départ doit être après la date d'arrivée"));
            }

            // ✅ Calcul avec BigDecimal
            BigDecimal prixParNuit = chambre.getPrixParNuit();
            BigDecimal prixTotalBD = prixParNuit.multiply(BigDecimal.valueOf(nombreNuits));
            Double prixTotal = prixTotalBD.doubleValue();

            System.out.println("💰 Prix par nuit: " + prixParNuit + " € | Nombre de nuits: " + nombreNuits + " | Prix total: " + prixTotal + " €");

            // Créer la réservation
            Reservation reservation = Reservation.builder()
                    .chambre(chambre)
                    .user(user)
                    .dateDebut(request.getDateDebut())
                    .dateFin(request.getDateFin())
                    .nomClient(request.getNomClient())
                    .emailClient(user.getEmail())
                    .telephoneClient(request.getTelephoneClient())
                    .nombrePersonnes(request.getNombrePersonnes())
                    .prixTotal(prixTotal)
                    .statut(Reservation.StatutReservation.CONFIRMEE)
                    .build();

            Reservation saved = reservationRepository.save(reservation);

            System.out.println("✅ Réservation créée : " + saved.getId() + " - Code: " + saved.getCodeConfirmation());

            // Envoyer l'email de confirmation
            try {
                emailService.envoyerEmailConfirmation(saved);
                System.out.println("✅ Email envoyé à : " + saved.getEmailClient());
            } catch (Exception e) {
                System.err.println("⚠️ Erreur envoi email : " + e.getMessage());
            }

            // ✅ Convertir en DTO
            ReservationResponseDTO responseDTO = convertToDTO(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/mes-reservations - Récupérer les réservations de l'utilisateur connecté
     */
    @GetMapping("/mes-reservations")
    public ResponseEntity<?> getMesReservations(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Veuillez vous connecter"));
        }

        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Reservation> reservations = reservationRepository.findByUserIdOrderByDateDebutDesc(user.getId());

            // ✅ Convertir en DTO
            List<ReservationResponseDTO> responseDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
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
                        .body(Map.of("error", "Aucune réservation trouvée avec ce code : " + code));
            }

            ReservationResponseDTO responseDTO = convertToDTO(reservation);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/user/{userId} - Récupérer toutes les réservations d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUserId(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationRepository.findByUserIdOrderByDateDebutDesc(userId);
            List<ReservationResponseDTO> responseDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reservations/{id} - Récupérer une réservation par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            return reservationRepository.findById(id)
                    .map(reservation -> ResponseEntity.ok(convertToDTO(reservation)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/reservations/{id}/annuler - Annuler une réservation
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerReservation(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Veuillez vous connecter"));
        }

        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            return reservationRepository.findById(id)
                    .map(reservation -> {
                        // Vérifier que la réservation appartient à l'utilisateur
                        if (reservation.getUser() == null || !reservation.getUser().getId().equals(user.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "Vous n'êtes pas autorisé à annuler cette réservation"));
                        }

                        reservation.setStatut(Reservation.StatutReservation.ANNULEE);
                        Reservation updated = reservationRepository.save(reservation);
                        return ResponseEntity.ok(convertToDTO(updated));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/reservations/{id} - Supprimer une réservation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerReservation(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Veuillez vous connecter"));
        }

        try {
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            return reservationRepository.findById(id)
                    .map(reservation -> {
                        // Vérifier que la réservation appartient à l'utilisateur
                        if (reservation.getUser() == null || !reservation.getUser().getId().equals(user.getId())) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "Vous n'êtes pas autorisé à supprimer cette réservation"));
                        }

                        reservationRepository.delete(reservation);
                        return ResponseEntity.ok(Map.of("message", "Réservation supprimée avec succès"));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/reservations - Récupérer toutes les réservations (Admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> reservations = reservationRepository.findAll();
            List<ReservationResponseDTO> responseDTOs = reservations.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ✅ Méthode utilitaire pour convertir Reservation en ReservationResponseDTO
     */
    private ReservationResponseDTO convertToDTO(Reservation reservation) {
        Chambre chambre = reservation.getChambre();

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .chambreId(chambre.getId())
                .chambreNom(chambre.getNom())
                .hotelId(chambre.getHotel().getId())
                .hotelNom(chambre.getHotel().getNom())
                .hotelVille(chambre.getHotel().getVille())
                .hotelImageUrl(chambre.getHotel().getImageUrl())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .nomClient(reservation.getNomClient())
                .emailClient(reservation.getEmailClient())
                .telephoneClient(reservation.getTelephoneClient())
                .nombrePersonnes(reservation.getNombrePersonnes())
                .prixTotal(BigDecimal.valueOf(reservation.getPrixTotal()))
                .statut(reservation.getStatut())
                .codeConfirmation(reservation.getCodeConfirmation())
                .build();
    }
}