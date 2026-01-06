package com.example.springhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChambreDTO {
    private Long id;
    private String nom;
    private BigDecimal prixParNuit;
    private Integer capacite;
    private Integer superficie;
    private String typeLit;
    private String description;
    private List<String> equipements;
    private List<String> imageUrls;
    private Long hotelId;
    private String hotelNom;
}