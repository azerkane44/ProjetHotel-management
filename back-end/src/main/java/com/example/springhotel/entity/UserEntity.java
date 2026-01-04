package com.example.springhotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Getters et setters
@Setter
@Getter
@Entity
@Table(name = "user_entity")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserEntity {

    // TODO : GenerationType.AUTO en GenerationType.IDENTITY pour fixer les ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private List<RoleEntity> roleEntities = new ArrayList<>();

    public List<RoleEntity> getRoles() { return roleEntities; }
    public void setRoles(List<RoleEntity> roleEntities) { this.roleEntities = roleEntities; }

    // Méthode utilitaire pour ajouter un rôle
    public void addRole(RoleEntity roleEntity) {
        this.roleEntities.add(roleEntity);
    }

    public void setRole(String roleAdmin) {
    }
}
