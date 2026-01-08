package com.example.springhotel.controller;

import com.example.springhotel.dto.ChambreDTO;
import com.example.springhotel.dto.DisponibiliteDTO;
import com.example.springhotel.service.ChambreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chambres")
@CrossOrigin(originPatterns = "http://localhost:*", allowCredentials = "true")
public class ChambreController {

    private final ChambreService chambreService;
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/chambres").toAbsolutePath();

    // ✅ Constructeur manuel (pas @RequiredArgsConstructor)
    public ChambreController(ChambreService chambreService) {
        this.chambreService = chambreService;

        // Créer le dossier uploads au démarrage
        try {
            Files.createDirectories(uploadDir);
            System.out.println("✅ Dossier uploads chambres créé : " + uploadDir);
        } catch (IOException e) {
            System.err.println("❌ Erreur création dossier uploads chambres : " + e.getMessage());
        }
    }

    /**
     * CREATE - Créer une nouvelle chambre avec images
     * POST /api/chambres
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ChambreDTO> creerChambre(
            @RequestParam String nom,
            @RequestParam String prixParNuit,  // ✅ String au lieu de Double
            @RequestParam Integer capacite,
            @RequestParam Integer superficie,
            @RequestParam String typeLit,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> equipements,
            @RequestParam Long hotelId,
            @RequestParam(required = false) List<MultipartFile> images
    ) {
        System.out.println("\n========== CRÉATION CHAMBRE ==========");
        System.out.println("📝 Nom : " + nom);
        System.out.println("💰 Prix : " + prixParNuit + "€");
        System.out.println("👥 Capacité : " + capacite);
        System.out.println("🖼️ Images : " + (images != null ? images.size() : 0));

        ChambreDTO chambreDTO = new ChambreDTO();
        chambreDTO.setNom(nom);
        chambreDTO.setPrixParNuit(new BigDecimal(prixParNuit));  // ✅ Conversion String -> BigDecimal
        chambreDTO.setCapacite(capacite);
        chambreDTO.setSuperficie(superficie);
        chambreDTO.setTypeLit(typeLit);
        chambreDTO.setDescription(description);
        chambreDTO.setEquipements(equipements != null ? equipements : new ArrayList<>());
        chambreDTO.setHotelId(hotelId);

        // Sauvegarder les images
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                        Path filePath = uploadDir.resolve(fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        imageUrls.add("/uploads/chambres/" + fileName);
                        System.out.println("✅ Image sauvegardée : " + fileName);
                    } catch (IOException e) {
                        System.err.println("❌ Erreur sauvegarde image : " + e.getMessage());
                    }
                }
            }
        }
        chambreDTO.setImageUrls(imageUrls);

        ChambreDTO created = chambreService.creerChambre(chambreDTO);
        System.out.println("✅ Chambre créée avec ID : " + created.getId());
        System.out.println("====================================\n");

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * READ ALL - Récupérer toutes les chambres
     * GET /api/chambres
     */
    @GetMapping
    public ResponseEntity<List<ChambreDTO>> getAllChambres() {
        List<ChambreDTO> chambres = chambreService.getAllChambres();
        return ResponseEntity.ok(chambres);
    }

    /**
     * READ BY ID - Récupérer une chambre par ID
     * GET /api/chambres/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChambreDTO> getChambreById(@PathVariable Long id) {
        ChambreDTO chambre = chambreService.getChambreById(id);
        return ResponseEntity.ok(chambre);
    }

    /**
     * READ BY HOTEL - Récupérer les chambres d'un hôtel
     * GET /api/chambres/hotel/{hotelId}
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ChambreDTO>> getChambresByHotel(@PathVariable Long hotelId) {
        List<ChambreDTO> chambres = chambreService.getChambresByHotel(hotelId);
        return ResponseEntity.ok(chambres);
    }

    /**
     * UPDATE - Modifier une chambre existante avec images
     * PUT /api/chambres/{id}
     */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ChambreDTO> updateChambre(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam String prixParNuit,  // ✅ String au lieu de Double
            @RequestParam Integer capacite,
            @RequestParam Integer superficie,
            @RequestParam String typeLit,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> equipements,
            @RequestParam Long hotelId,
            @RequestParam(required = false) List<MultipartFile> images
    ) {
        System.out.println("\n========== MODIFICATION CHAMBRE ==========");
        System.out.println("🆔 ID : " + id);
        System.out.println("📝 Nom : " + nom);

        ChambreDTO chambreDTO = chambreService.getChambreById(id);
        chambreDTO.setNom(nom);
        chambreDTO.setPrixParNuit(new BigDecimal(prixParNuit));  // ✅ Conversion String -> BigDecimal
        chambreDTO.setCapacite(capacite);
        chambreDTO.setSuperficie(superficie);
        chambreDTO.setTypeLit(typeLit);
        chambreDTO.setDescription(description);
        chambreDTO.setEquipements(equipements != null ? equipements : new ArrayList<>());
        chambreDTO.setHotelId(hotelId);

        // Ajouter les nouvelles images
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = new ArrayList<>(chambreDTO.getImageUrls());
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                        Path filePath = uploadDir.resolve(fileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        imageUrls.add("/uploads/chambres/" + fileName);
                        System.out.println("✅ Nouvelle image ajoutée : " + fileName);
                    } catch (IOException e) {
                        System.err.println("❌ Erreur sauvegarde image : " + e.getMessage());
                    }
                }
            }
            chambreDTO.setImageUrls(imageUrls);
        }

        ChambreDTO updated = chambreService.updateChambre(id, chambreDTO);
        System.out.println("✅ Chambre modifiée");
        System.out.println("========================================\n");

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE - Supprimer une chambre
     * DELETE /api/chambres/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id) {
        System.out.println("\n========== SUPPRESSION CHAMBRE ==========");
        System.out.println("🆔 ID : " + id);

        ChambreDTO chambre = chambreService.getChambreById(id);

        // Supprimer les images
        if (chambre.getImageUrls() != null) {
            for (String imageUrl : chambre.getImageUrls()) {
                if (imageUrl != null && !imageUrl.startsWith("http")) {
                    try {
                        Path imagePath = Paths.get("src/main/resources/static" + imageUrl);
                        Files.deleteIfExists(imagePath);
                        System.out.println("🗑️ Image supprimée : " + imagePath);
                    } catch (IOException e) {
                        System.err.println("❌ Erreur suppression image : " + e.getMessage());
                    }
                }
            }
        }

        chambreService.deleteChambre(id);
        System.out.println("✅ Chambre supprimée");
        System.out.println("=======================================\n");

        return ResponseEntity.noContent().build();
    }

    /**
     * DISPONIBILITÉ - Récupérer les chambres disponibles entre deux dates
     * GET /api/chambres/disponibles?dateDebut=YYYY-MM-DD&dateFin=YYYY-MM-DD&hotelId=X
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<ChambreDTO>> getChambresDisponibles(
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam(required = false) Long hotelId
    ) {
        DisponibiliteDTO request = new DisponibiliteDTO();
        request.setDateDebut(java.time.LocalDate.parse(dateDebut));
        request.setDateFin(java.time.LocalDate.parse(dateFin));
        request.setHotelId(hotelId);

        List<ChambreDTO> chambres = chambreService.getChambresDisponibles(request);
        return ResponseEntity.ok(chambres);
    }
}

