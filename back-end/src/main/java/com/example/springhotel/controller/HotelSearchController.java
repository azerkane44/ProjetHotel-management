package com.example.springhotel.controller;

import com.example.springhotel.dto.HotelSearchDTO;
import com.example.springhotel.entity.Hotel;
import com.example.springhotel.service.HotelSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class HotelSearchController {

    private final HotelSearchService hotelSearchService;

    @PostMapping
    public ResponseEntity<List<Hotel>> searchHotels(@RequestBody HotelSearchDTO searchDTO) {
        List<Hotel> hotels = hotelSearchService.searchHotels(searchDTO);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/by-location")
    public ResponseEntity<List<Hotel>> getHotelsByLocation(
            @RequestParam String ville,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10") Double radiusKm) {

        List<Hotel> hotels = hotelSearchService.findHotelsByLocation(
                ville, latitude, longitude, radiusKm
        );
        return ResponseEntity.ok(hotels);
    }
}