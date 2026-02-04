package com.app.savings.services;

import com.app.savings.entities.Usuario;
import com.app.savings.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Service
@org.springframework.transaction.annotation.Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Autowired
    private FinanzaRepository finanzaRepository;

    @Autowired
    private HistorialInformeRepository historialInformeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                    .password(passwordEncoder.encode("Admin1234"))
                    .email("admin@easysave.com")
                    .perfil(perfilUsuarioRepository.findByNombre("ADMIN").orElse(null))
                    .build();
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN creado por defecto");
        }

        if (usuarioRepository.findByUsername("katherin").isEmpty()) {
            Usuario user = Usuario.builder()
                    .username("katherin")
                    .password(passwordEncoder.encode("Kate1234"))
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

        // Caso 1: Cambio de username (PK)
        if (!usuarioExistente.getUsername().equals(usuarioActualizado.getUsername())) {
            if (usuarioRepository.findByUsername(usuarioActualizado.getUsername()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya existe");
            }

            // [FIX] Liberar el email del usuario antiguo temporalmente para evitar Unique Constraint
            String oldEmail = usuarioExistente.getEmail();
            usuarioExistente.setEmail(oldEmail + "_temp_" + System.currentTimeMillis());
            usuarioRepository.saveAndFlush(usuarioExistente);

            // Crear nuevo usuario con los datos actualizados
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(usuarioActualizado.getUsername());
            nuevoUsuario.setEmail(usuarioActualizado.getEmail());
            
            // Password
             if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                nuevoUsuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
            } else {
                nuevoUsuario.setPassword(usuarioExistente.getPassword());
            }

             // Perfil
            if (usuarioActualizado.getPerfil() != null) {
                nuevoUsuario.setPerfil(perfilUsuarioRepository.findByNombre(usuarioActualizado.getPerfil().getNombre()).orElse(null));
            } else {
                nuevoUsuario.setPerfil(usuarioExistente.getPerfil());
            }

            // Guardar el nuevo usuario
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);

            // Migrar Finanzas
            java.util.List<com.app.savings.entities.Finanza> finanzas = finanzaRepository.findByUsuarioUsername(id);
            for(com.app.savings.entities.Finanza f : finanzas) {
                f.setUsuario(nuevoUsuario);
                finanzaRepository.save(f);
            }

            // Migrar Informes
            // IMPORTANTE: Usar el objeto usuarioExistente actualizado (tiene email cambiado pero mismo ID/Username)
            java.util.List<com.app.savings.entities.HistorialInforme> informes = historialInformeRepository.findByUsuario(usuarioExistente);
            for(com.app.savings.entities.HistorialInforme i : informes) {
                i.setUsuario(nuevoUsuario);
                historialInformeRepository.save(i);
            }

            // Eliminar el usuario antiguo
            usuarioRepository.delete(usuarioExistente);

            return nuevoUsuario;
        }

        // Caso 2: El username no cambia (Update normal)

        // Validar que el email no esté tomado por otro usuario
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioActualizado.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya existe");
            }
        }

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

    public void deleteUsuario(String username) {
        if (!usuarioRepository.existsById(username)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        // Primero eliminar dependencias o setear a null si fuera necesario,
        // pero JPA Cascade podría manejarlo si estuviera configurado.
        // Como no tengo CascadeType.ALL visible en las entidades, debería borrar
        // manualmente si hay constraints.
        // Asumiendo que OnDelete Action en DB es RESTRICT, debo borrar hijos.

        Usuario u = usuarioRepository.findById(username).get();

        java.util.List<com.app.savings.entities.Finanza> finanzas = finanzaRepository.findByUsuarioUsername(username);
        finanzaRepository.deleteAll(finanzas);

        java.util.List<com.app.savings.entities.HistorialInforme> informes = historialInformeRepository
                .findByUsuario(u);
        historialInformeRepository.deleteAll(informes);

        usuarioRepository.deleteById(username);
    }
}
