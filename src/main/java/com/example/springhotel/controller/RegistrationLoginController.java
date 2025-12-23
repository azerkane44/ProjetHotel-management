package com.example.springhotel.controller;

import com.example.springhotel.entity.Role;
import com.example.springhotel.entity.User;
import com.example.springhotel.repository.RoleRepository;
import com.example.springhotel.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
public class RegistrationLoginController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationLoginController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {

        // 🔍 Validation simple
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email obligatoire"));
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Mot de passe trop court (min 6 caractères)"));
        }

        // 🔴 Email déjà utilisé
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email déjà utilisé"));
        }

        // 🔐 Rôle USER
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "ROLE_USER manquant en base"));
        }

        // 👤 Création utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(List.of(userRole));

        userRepository.save(user);

        // ✅ Réponse clean pour React
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Utilisateur créé avec succès",
                        "id", user.getId(),
                        "email", user.getEmail()
                ));
    }

    // 🔹 DTO
    static class RegistrationRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
