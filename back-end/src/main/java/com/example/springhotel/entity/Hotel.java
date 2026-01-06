package com.example.springhotel.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String ville;
    private String adresse;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "note_moyenne")
    private Double noteMoyenne;

    @Column(name = "image_url")
    private String imageUrl;

    // ⭐ NOUVEAUX CHAMPS POUR LA RECHERCHE AVANCÉE
    private Double latitude;
    private Double longitude;

    @Column(name = "prix_moyen_nuit")
    private Double prixMoyenNuit;

    private Integer categorie; // Nombre d'étoiles (1-5)

    @ElementCollection
    @CollectionTable(name = "hotel_equipements", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "equipement")
    private List<String> equipements;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @JsonManagedReference  // ✅ Ajout de cette annotation
    private List<Chambre> chambres;
}