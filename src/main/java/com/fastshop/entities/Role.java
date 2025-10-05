package com.fastshop.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data // Inclui getters, setters, toString, e EqualsAndHashCode
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O campo que o Spring Security usa para autorização (ex: "ROLE_ADMIN")
    private String authority;

    // Construtor útil para o Seeding (criação de roles no Flyway)
    public Role(String authority) {
        this.authority = authority;
    }

    // OBRIGATÓRIO: Implementação da interface GrantedAuthority
    @Override
    public String getAuthority() {
        return authority;
    }
}