package com.example.springhotel.service;

import com.example.springhotel.dto.HotelSearchDTO;
import com.example.springhotel.entity.Hotel;
import com.example.springhotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelSearchService {

    private final HotelRepository hotelRepository;

    public List<Hotel> searchHotels(HotelSearchDTO searchDTO) {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .filter(hotel -> filterByDestination(hotel, searchDTO.getDestination()))
                .filter(hotel -> filterByPrice(hotel, searchDTO.getPrixMin(), searchDTO.getPrixMax()))
                .filter(hotel -> filterByCategory(hotel, searchDTO.getCategories()))
                .filter(hotel -> filterByRating(hotel, searchDTO.getNotationMin()))
                .filter(hotel -> filterByEquipments(hotel, searchDTO.getEquipements()))
                .filter(hotel -> filterByLocation(hotel, searchDTO.getLatitude(),
                        searchDTO.getLongitude(), searchDTO.getRadiusKm()))
                .collect(Collectors.toList());
    }

    public List<Hotel> findHotelsByLocation(String ville, Double latitude,
                                            Double longitude, Double radiusKm) {
        List<Hotel> hotels = hotelRepository.findAll();

        return hotels.stream()
                .filter(hotel -> {
                    if (ville != null && !ville.isEmpty()) {
                        return hotel.getVille().toLowerCase().contains(ville.toLowerCase());
                    }
                    if (latitude != null && longitude != null) {
                        return isWithinRadius(hotel, latitude, longitude, radiusKm);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private boolean filterByDestination(Hotel hotel, String destination) {
        if (destination == null || destination.isEmpty()) return true;
        return hotel.getVille().toLowerCase().contains(destination.toLowerCase()) ||
                hotel.getAdresse().toLowerCase().contains(destination.toLowerCase());
    }

    private boolean filterByPrice(Hotel hotel, Double prixMin, Double prixMax) {
        if (prixMin == null && prixMax == null) return true;
        double hotelPrice = hotel.getPrixMoyenNuit();
        if (prixMin != null && hotelPrice < prixMin) return false;
        if (prixMax != null && hotelPrice > prixMax) return false;
        return true;
    }

    private boolean filterByCategory(Hotel hotel, List<Integer> categories) {
        if (categories == null || categories.isEmpty()) return true;
        return categories.contains(hotel.getCategorie());
    }

    private boolean filterByRating(Hotel hotel, Double notationMin) {
        if (notationMin == null) return true;
        return hotel.getNoteMoyenne() >= notationMin;
    }

    private boolean filterByEquipments(Hotel hotel, List<String> equipements) {
        if (equipements == null || equipements.isEmpty()) return true;

        List<String> hotelEquipements = hotel.getEquipements();
        if (hotelEquipements == null) return false;

        return hotelEquipements.containsAll(equipements);
    }

    private boolean filterByLocation(Hotel hotel, Double latitude,
                                     Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null) return true;
        return isWithinRadius(hotel, latitude, longitude, radiusKm);
    }

    private boolean isWithinRadius(Hotel hotel, Double latitude,
                                   Double longitude, Double radiusKm) {
        if (hotel.getLatitude() == null || hotel.getLongitude() == null) return false;

        double distance = calculateDistance(
                latitude, longitude,
                hotel.getLatitude(), hotel.getLongitude()
        );

        return distance <= radiusKm;
    }

    // Formule de Haversine
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Rayon de la Terre en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}