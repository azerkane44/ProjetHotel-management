package com.example.springhotel.controller;

import com.example.springhotel.entity.Role;
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
        System.out.println("\n========== TENTATIVE DE CONNEXION ==========");
        System.out.println("📧 Email reçu : " + loginRequest.getEmail());
        System.out.println("🔑 Mot de passe reçu : " + loginRequest.getPassword());

        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            System.out.println("❌ Utilisateur non trouvé dans la base");
            System.out.println("============================================\n");
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }

        User existingUser = userOptional.get();

        System.out.println("✅ Utilisateur trouvé :");
        System.out.println("   - ID : " + existingUser.getId());
        System.out.println("   - Email : " + existingUser.getEmail());
        System.out.println("   - Enabled : " + existingUser.isEnabled());
        System.out.println("   - Hash en base : " + existingUser.getPassword());
        System.out.println("   - Nombre de rôles : " + existingUser.getRoles().size());

        if (existingUser.getRoles() != null && !existingUser.getRoles().isEmpty()) {
            System.out.println("   - Rôles : " + existingUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        } else {
            System.out.println("   - ⚠️ AUCUN RÔLE ASSIGNÉ !");
        }

        System.out.println("\n🔐 Vérification du mot de passe...");
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword());
        System.out.println("   - Mot de passe saisi : " + loginRequest.getPassword());
        System.out.println("   - Hash en base : " + existingUser.getPassword());
        System.out.println("   - Résultat : " + (passwordMatches ? "✅ MATCH" : "❌ NO MATCH"));

        if (!passwordMatches) {
            System.out.println("❌ Échec de connexion : mot de passe incorrect");
            System.out.println("============================================\n");
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

        System.out.println("✅ Connexion réussie - Token généré");
        System.out.println("   - Token : " + token.substring(0, Math.min(50, token.length())) + "...");
        System.out.println("   - Rôles dans le token : " + roles);
        System.out.println("============================================\n");

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
