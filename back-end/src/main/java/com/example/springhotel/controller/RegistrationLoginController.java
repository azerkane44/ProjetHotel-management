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
import java.util.Optional;

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
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest request,
            @RequestParam(defaultValue = "USER") String role
    ) {

        System.out.println("📥 Tentative d'inscription avec email : " + request.getEmail());

        // 🔍 Validation
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "L'email est obligatoire"));
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le mot de passe doit contenir au moins 6 caractères"));
        }

        // ✅ CORRECTION : Vérification si l'email existe déjà (avec Optional)
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        System.out.println("🔍 Email existe déjà ? " + existingUser.isPresent());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Cet email est déjà utilisé. Veuillez en choisir un autre."));
        }

        // 🔐 Récupère le rôle
        Role userRole = roleRepository.findByName("ROLE_" + role.toUpperCase());
        if (userRole == null) {
            System.err.println("❌ Rôle ROLE_" + role.toUpperCase() + " introuvable en base");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur de configuration : rôle manquant"));
        }

        // 👤 Création utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(List.of(userRole));

        User savedUser = userRepository.save(user);

        System.out.println("✅ Utilisateur créé avec ID : " + savedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Compte créé avec succès !",
                        "id", savedUser.getId(),
                        "email", savedUser.getEmail()
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
