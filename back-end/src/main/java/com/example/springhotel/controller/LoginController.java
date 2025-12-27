package com.example.springhotel.controller;

import com.example.springhotel.entity.UserEntity;
import com.example.springhotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173") // autorise React
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        UserEntity existingUser = userRepository.findByEmail(loginRequest.getEmail());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("Mot de passe incorrect");
        }

        // Connexion réussie → renvoyer les infos utiles avec les rôles
        List<String> roles = existingUser.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserResponse(existingUser.getId(), existingUser.getEmail(), roles));
    }

    // Classe interne pour la requête de login
    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Classe interne pour la réponse (sans password)
    static class UserResponse {
        private Long id;
        private String email;
        private List<String> roles;

        public UserResponse(Long id, String email, List<String> roles) {
            this.id = id;
            this.email = email;
            this.roles = roles;
        }

        public Long getId() { return id; }
        public String getEmail() { return email; }
        public List<String> getRoles() { return roles; }
    }
}
