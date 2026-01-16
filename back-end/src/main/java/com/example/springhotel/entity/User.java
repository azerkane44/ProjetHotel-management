package com.example.springhotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users")
public class User {

    // Getters et setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    // MÉTHODES UTILITAIRES POUR COMPATIBILITÉ
    public String getNom() {
        return lastName;
    }

    public String getPrenom() {
        return firstName;
    }

    // Méthode utilitaire pour ajouter un rôle
    public void addRole(Role role) {
        this.roles.add(role);
    }

//    public void setRole(String roleAdmin) {
//        // Implémentation si nécessaire
//    }

//    public User orElseThrow(Object utilisateurNonTrouvé) {
//        return null;
//    }
}