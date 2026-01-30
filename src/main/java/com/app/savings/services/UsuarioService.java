package com.app.savings.services;

import com.app.savings.entities.Usuario;
import com.app.savings.repository.PerfilUsuarioRepository;
import com.app.savings.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public Usuario login(String usernameOrEmail, String password) {
        return usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElse(null);
    }

    public Usuario register(Usuario usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya existe");
        }

        // Por defecto rol USER si no viene especificado
        if (usuario.getPerfil() == null) {
            usuario.setPerfil(perfilUsuarioRepository.findByNombre("USER").orElse(null));
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public void initAdmin() {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin1234"))
                    .email("admin@easysave.com")
                    .perfil(perfilUsuarioRepository.findByNombre("ADMIN").orElse(null))
                    .build();
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN creado por defecto");
        }

        if (usuarioRepository.findByUsername("katherin").isEmpty()) {
            Usuario user = Usuario.builder()
                    .username("katherin")
                    .password(passwordEncoder.encode("kate1234"))
                    .email("katherin@easysave.com")
                    .perfil(perfilUsuarioRepository.findByNombre("USER").orElse(null))
                    .build();
            usuarioRepository.save(user);
            System.out.println("Usuario KATHERIN creado por defecto");
        }
    }

    public java.util.List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
