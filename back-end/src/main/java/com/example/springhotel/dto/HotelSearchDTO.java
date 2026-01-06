package com.example.springhotel.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class HotelSearchDTO {
    private String destination;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private Integer nombreVoyageurs;

    // Filtres
    private Double prixMin;
    private Double prixMax;
    private List<Integer> categories;
    private Double notationMin;
    private List<String> equipements;

    // Géolocalisation
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
}