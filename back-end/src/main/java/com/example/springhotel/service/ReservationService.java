package com.example.springhotel.service;

import com.example.springhotel.dto.ReservationRequestDTO;
import com.example.springhotel.dto.ReservationResponseDTO;
import com.example.springhotel.entity.Chambre;
import com.example.springhotel.entity.Reservation;
import com.example.springhotel.entity.User;
import com.example.springhotel.repository.ChambreRepository;
import com.example.springhotel.repository.ReservationRepository;
import com.example.springhotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * ✅ Créer une nouvelle réservation
     */
    @Transactional
    public ReservationResponseDTO creerReservation(ReservationRequestDTO request, String userEmail) {
        // Vérifier que l'utilisateur est connecté
        if (userEmail == null) {
            throw new RuntimeException("Vous devez être connecté pour réserver");
        }

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que la chambre existe
        Chambre chambre = chambreRepository.findById(request.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée"));

        // Vérifier la disponibilité
        boolean isOccupied = reservationRepository.existsReservationChevauchante(
                request.getChambreId(),
                request.getDateDebut(),
                request.getDateFin()
        );

        if (isOccupied) {
            throw new RuntimeException("Cette chambre n'est pas disponible pour ces dates");
        }

        // Calculer le nombre de nuits
        long nombreNuits = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin());
        if (nombreNuits <= 0) {
            throw new RuntimeException("La date de départ doit être après la date d'arrivée");
        }

        // ✅ Calculer le prix total avec BigDecimal
        BigDecimal prixParNuit = chambre.getPrixParNuit();
        BigDecimal prixTotalBD = prixParNuit.multiply(BigDecimal.valueOf(nombreNuits));
        Double prixTotal = prixTotalBD.doubleValue();

        // ✅ Utiliser l'email de l'utilisateur connecté
        String emailClient = user.getEmail();

        // Créer la réservation
        Reservation reservation = Reservation.builder()
                .chambre(chambre)
                .user(user)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .nomClient(request.getNomClient())
                .emailClient(emailClient) // ✅ Email de l'utilisateur connecté
                .telephoneClient(request.getTelephoneClient())
                .nombrePersonnes(request.getNombrePersonnes())
                .prixTotal(prixTotal)
                .statut(Reservation.StatutReservation.CONFIRMEE)
                .codeConfirmation(genererCodeConfirmation())
                .build();

        Reservation saved = reservationRepository.save(reservation);

        // ✅ Envoyer l'email de confirmation
        try {
            emailService.envoyerEmailConfirmation(saved);
            System.out.println("✅ Email de confirmation envoyé à : " + emailClient);
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }

        return convertToDTO(saved);
    }

    /**
     * Récupérer toutes les réservations d'un utilisateur
     */
    public List<ReservationResponseDTO> getReservationsByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByDateDebutDesc(userId);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une réservation par code de confirmation
     */
    public ReservationResponseDTO getReservationByCode(String code) {
        Reservation reservation = reservationRepository.findByCodeConfirmation(code);
        if (reservation == null) {
            throw new RuntimeException("Aucune réservation trouvée avec ce code");
        }
        return convertToDTO(reservation);
    }

    /**
     * Annuler une réservation
     */
    @Transactional
    public ReservationResponseDTO annulerReservation(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier que la réservation appartient à l'utilisateur
        if (reservation.getUser() == null || !reservation.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette réservation");
        }

        reservation.setStatut(Reservation.StatutReservation.ANNULEE);
        Reservation updated = reservationRepository.save(reservation);

        return convertToDTO(updated);
    }

    /**
     * Vérifier la disponibilité d'une chambre
     */
    public boolean verifierDisponibilite(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        return !reservationRepository.existsReservationChevauchante(chambreId, dateDebut, dateFin);
    }

    /**
     * Récupérer les réservations futures d'un utilisateur
     */
    public List<ReservationResponseDTO> getReservationsFutures(Long userId) {
        List<Reservation> reservations = reservationRepository.findUpcomingReservationsByUserId(userId, LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les réservations passées d'un utilisateur
     */
    public List<ReservationResponseDTO> getReservationsPassees(Long userId) {
        List<Reservation> reservations = reservationRepository.findPastReservationsByUserId(userId, LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Générer un code de confirmation unique
     */
    private String genererCodeConfirmation() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * ✅ Convertir Reservation en ReservationResponseDTO
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