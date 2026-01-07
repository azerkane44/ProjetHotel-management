package com.example.springhotel.controller;

import com.example.springhotel.entity.User;
import com.example.springhotel.repository.UserRepository;
import com.example.springhotel.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }

        User existingUser = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("Mot de passe incorrect");
        }

        // ✅ Extraire les rôles
        List<String> roles = existingUser.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        // ✅ Générer le token JWT
        String token = jwtUtil.generateToken(
                existingUser.getEmail(),
                existingUser.getId(),
                roles
        );

        System.out.println("✅ Token généré pour : " + existingUser.getEmail());

        // ✅ Renvoyer le token avec les infos utilisateur
        return ResponseEntity.ok(new UserResponse(
                token,
                existingUser.getId(),
                existingUser.getEmail(),
                existingUser.getFirstName(),
                existingUser.getLastName(),
                roles
        ));
    }

    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class UserResponse {
        private String token;
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private List<String> roles;

        public UserResponse(String token, Long id, String email, String firstName, String lastName, List<String> roles) {
            this.token = token;
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
        }

        public String getToken() { return token; }
        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public List<String> getRoles() { return roles; }
    }
}