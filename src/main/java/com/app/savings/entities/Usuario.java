package com.app.savings.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;
    @ManyToOne
    private PerfilUsuario perfil;

}
