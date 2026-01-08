package com.example.springhotel.controller;

import com.example.springhotel.entity.Hotel;
import com.example.springhotel.repository.HotelRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:5173")
public class HotelController {

    private final HotelRepository hotelRepository;
    private final Path uploadDir = Paths.get("src/main/resources/static/uploads/hotels").toAbsolutePath();

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;

        // Créer le dossier uploads au démarrage
        try {
            Files.createDirectories(uploadDir);
            System.out.println("✅ Dossier uploads créé : " + uploadDir);
        } catch (IOException e) {
            System.err.println("❌ Erreur création dossier uploads : " + e.getMessage());
        }
    }

    // 🔹 GET — Tous les hôtels
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // 🔹 GET — Hôtel par ID
    @GetMapping("/{id}")
    public Optional<Hotel> getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id);
    }

    // 🔹 POST — CRÉER un hôtel avec image
    @PostMapping(consumes = "multipart/form-data")
    public Hotel createHotelWithImage(
            @RequestParam String nom,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String pays,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") double noteMoyenne,
            @RequestParam(required = false) MultipartFile image
    ) {
        System.out.println("\n========== CRÉATION HÔTEL ==========");
        System.out.println("📝 Nom : " + nom);
        System.out.println("📍 Ville : " + ville);
        System.out.println("🌍 Pays : " + pays);
        System.out.println("📄 Description : " + (description != null ? description.substring(0, Math.min(50, description.length())) + "..." : "Aucune"));
        System.out.println("🖼️ Image : " + (image != null ? image.getOriginalFilename() : "Aucune"));

        Hotel hotel = new Hotel();
        hotel.setNom(nom);
        hotel.setAdresse(adresse);
        hotel.setVille(ville);
        hotel.setPays(pays);
        hotel.setDescription(description);
        hotel.setNoteMoyenne(noteMoyenne);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                hotel.setImageUrl("/uploads/hotels/" + fileName);
                System.out.println("✅ Image sauvegardée : " + filePath);
                System.out.println("🔗 URL : " + hotel.getImageUrl());
            } catch (IOException e) {
                System.err.println("❌ Erreur sauvegarde image : " + e.getMessage());
                e.printStackTrace();
            }
        }

        Hotel saved = hotelRepository.save(hotel);
        System.out.println("✅ Hôtel créé avec ID : " + saved.getId());
        System.out.println("====================================\n");

        return saved;
    }

    // 🔹 PUT — MODIFIER un hôtel avec image
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Hotel updateHotelWithImage(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String pays,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") double noteMoyenne,
            @RequestParam(required = false) MultipartFile image
    ) {
        System.out.println("\n========== MODIFICATION HÔTEL ==========");
        System.out.println("🆔 ID : " + id);
        System.out.println("📝 Nom : " + nom);
        System.out.println("📍 Ville : " + ville);
        System.out.println("🌍 Pays : " + pays);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel introuvable"));

        hotel.setNom(nom);
        hotel.setAdresse(adresse);
        hotel.setVille(ville);
        hotel.setPays(pays);
        hotel.setDescription(description);
        hotel.setNoteMoyenne(noteMoyenne);

        if (image != null && !image.isEmpty()) {
            try {
                // Supprimer l'ancienne image
                if (hotel.getImageUrl() != null && !hotel.getImageUrl().startsWith("http")) {
                    Path oldImagePath = Paths.get("src/main/resources/static" + hotel.getImageUrl());
                    Files.deleteIfExists(oldImagePath);
                    System.out.println("🗑️ Ancienne image supprimée");
                }

                // Sauvegarder la nouvelle image
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                hotel.setImageUrl("/uploads/hotels/" + fileName);
                System.out.println("✅ Nouvelle image sauvegardée : " + fileName);
            } catch (IOException e) {
                System.err.println("❌ Erreur modification image : " + e.getMessage());
            }
        }

        Hotel saved = hotelRepository.save(hotel);
        System.out.println("✅ Hôtel modifié");
        System.out.println("========================================\n");

        return saved;
    }

    // 🔹 DELETE — Supprimer hôtel + image
    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable Long id) {
        System.out.println("\n========== SUPPRESSION HÔTEL ==========");
        System.out.println("🆔 ID : " + id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel introuvable"));

        // Supprimer l'image
        if (hotel.getImageUrl() != null && !hotel.getImageUrl().startsWith("http")) {
            try {
                Path imagePath = Paths.get("src/main/resources/static" + hotel.getImageUrl());
                Files.deleteIfExists(imagePath);
                System.out.println("🗑️ Image supprimée : " + imagePath);
            } catch (IOException e) {
                System.err.println("❌ Erreur suppression image : " + e.getMessage());
            }
        }

        hotelRepository.delete(hotel);
        System.out.println("✅ Hôtel supprimé");
        System.out.println("=======================================\n");
    }
}
