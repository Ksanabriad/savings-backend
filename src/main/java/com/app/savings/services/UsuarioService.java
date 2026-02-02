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

        // Gestionar el perfil/rol
        if (usuario.getPerfil() != null && usuario.getPerfil().getNombre() != null) {
            usuario.setPerfil(perfilUsuarioRepository.findByNombre(usuario.getPerfil().getNombre())
                    .orElse(perfilUsuarioRepository.findByNombre("USER").orElse(null)));
        } else {
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

    public Usuario findById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario updateUsuario(String id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElse(null);
        if (usuarioExistente == null) {
            return null;
        }

        // Validar que el username no esté tomado por otro usuario
        if (!usuarioExistente.getUsername().equals(usuarioActualizado.getUsername())) {
            if (usuarioRepository.findByUsername(usuarioActualizado.getUsername()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }
        }

        // Validar que el email no esté tomado por otro usuario
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioActualizado.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya existe");
            }
        }

        usuarioExistente.setUsername(usuarioActualizado.getUsername());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());

        // Si hay una nueva contraseña, actualizarla
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        // Actualizar perfil
        if (usuarioActualizado.getPerfil() != null) {
            usuarioExistente.setPerfil(
                    perfilUsuarioRepository.findByNombre(usuarioActualizado.getPerfil().getNombre()).orElse(null));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
