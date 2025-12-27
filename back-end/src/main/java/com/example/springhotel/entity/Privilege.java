package com.example.springhotel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Entity
public class Privilege {

    // Getters et setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String name;

    @ManyToMany(mappedBy = "privileges")
    @JsonIgnore // Evite la boucle infinie
    private Collection<RoleEntity> roleEntities;

    public Privilege() {}
    public Privilege(String name) { this.name = name; }

    public Collection<RoleEntity> getRoles() { return roleEntities; }
    public void setRoles(Collection<RoleEntity> roleEntities) { this.roleEntities = roleEntities; }
}
