package com.example.springhotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String emailClient;

    @Column(nullable = false)
    private String telephoneClient;

    @Column(nullable = false)
    private Integer nombrePersonnes;

    @Column(nullable = false)
    private Double prixTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // permet d’affecter une valeur par défaut, à un attribut si elle n’est pas renseignée lors de la création de l'instance
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(unique = true)
    private String codeConfirmation;

    @ManyToOne(fetch = FetchType.LAZY) // Les entités en relation ne sont chargées qu’au moment de l’accès
    @JoinColumn(name = "chambre_id", nullable = false)
    @JsonIgnore
    private Chambre chambre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.codeConfirmation == null) {
            this.codeConfirmation = generateCodeConfirmation();
        }
    }

    private String generateCodeConfirmation() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public enum StatutReservation {
        EN_ATTENTE,
        CONFIRMEE,
        ANNULEE,
        TERMINEE
    }
}