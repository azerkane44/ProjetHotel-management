package com.example.springhotel.service;

import com.example.springhotel.entity.Privilege;
import com.example.springhotel.entity.RoleEntity;
import com.example.springhotel.entity.UserEntity;
import com.example.springhotel.repository.RoleRepository;
import com.example.springhotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(email);

        // ✅ COMPORTEMENT NORMAL
        if (user == null) {
            throw new UsernameNotFoundException(
                    "Utilisateur introuvable avec l'email : " + email
            );
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                getAuthorities(user.getRoles())
        );
    }

    // 🔐 Rôles + privilèges (sécurisé)
    private Collection<? extends GrantedAuthority> getAuthorities(Collection<RoleEntity> roleEntities) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (RoleEntity roleEntity : roleEntities) {
            // ✅ ROLE_XXX
            authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));

            // ✅ PRIVILEGES (null-safe)
            if (roleEntity.getPrivileges() != null) {
                for (Privilege privilege : roleEntity.getPrivileges()) {
                    authorities.add(new SimpleGrantedAuthority(privilege.getName()));
                }
            }
        }

        return authorities;
    }
}
