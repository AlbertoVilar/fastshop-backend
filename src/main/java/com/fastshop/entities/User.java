package com.fastshop.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username; // email

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    // --- MÉTODOS OBRIGATÓRIOS DO USERDETAILS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Isso funcionará SE a Entidade Role implementar GrantedAuthority
        return this.roles;
    }

    // Retorna TRUE por padrão para que a conta esteja ativa
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Retorna TRUE por padrão para que a conta não esteja bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Retorna TRUE por padrão para que as credenciais não estejam expiradas
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Retorna TRUE por padrão (para que o usuário possa fazer login)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
