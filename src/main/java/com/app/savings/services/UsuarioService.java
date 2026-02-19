package com.app.savings.services;

import com.app.savings.entities.Usuario;
import com.app.savings.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import com.app.savings.entities.*;

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

        // Determinar si hay cambio de username (Primary Key)
        boolean usernameChanged = !usuarioExistente.getUsername().equals(usuarioActualizado.getUsername());

        if (usernameChanged) {
            return handleUsernameChange(id, usuarioExistente, usuarioActualizado);
        } else {
            return updateExistingUsuario(usuarioExistente, usuarioActualizado);
        }
    }

    /**
     * Maneja el caso especial de cambio de username (Primary Key).
     * Crea un nuevo usuario, migra los datos relacionados y elimina el antiguo.
     */
    private Usuario handleUsernameChange(String oldUsername, Usuario usuarioExistente, Usuario usuarioActualizado) {
        // Validar que el nuevo username no exista
        validateUsernameNotTaken(usuarioActualizado.getUsername());

        // Liberar el email temporalmente para evitar conflictos de unique constraint
        String originalEmail = usuarioExistente.getEmail();
        usuarioExistente.setEmail(originalEmail + "_temp_" + System.currentTimeMillis());
        usuarioRepository.saveAndFlush(usuarioExistente);

        // Crear el nuevo usuario con los datos actualizados
        Usuario nuevoUsuario = createNewUsuarioFromUpdate(usuarioExistente, usuarioActualizado);
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // Migrar todos los datos relacionados al nuevo usuario
        migrateRelatedData(oldUsername, usuarioExistente, nuevoUsuario);

        // Eliminar el usuario antiguo
        usuarioRepository.delete(usuarioExistente);

        return nuevoUsuario;
    }

    /**
     * Actualiza un usuario existente sin cambiar el username.
     */
    private Usuario updateExistingUsuario(Usuario usuarioExistente, Usuario usuarioActualizado) {
        // Validar email si ha cambiado
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail())) {
            validateEmailNotTaken(usuarioActualizado.getEmail());
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }

        // Actualizar password si se proporciona uno nuevo
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        // Actualizar perfil si se proporciona
        if (usuarioActualizado.getPerfil() != null) {
            PerfilUsuario perfil = perfilUsuarioRepository
                    .findByNombre(usuarioActualizado.getPerfil().getNombre())
                    .orElse(null);
            usuarioExistente.setPerfil(perfil);
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Crea un nuevo objeto Usuario basado en los datos del usuario existente
     * y los datos de actualización proporcionados.
     */
    private Usuario createNewUsuarioFromUpdate(Usuario usuarioExistente, Usuario usuarioActualizado) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(usuarioActualizado.getUsername());
        nuevoUsuario.setEmail(usuarioActualizado.getEmail());

        // Configurar password
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            nuevoUsuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        } else {
            nuevoUsuario.setPassword(usuarioExistente.getPassword());
        }

        // Configurar perfil
        if (usuarioActualizado.getPerfil() != null) {
            PerfilUsuario perfil = perfilUsuarioRepository
                    .findByNombre(usuarioActualizado.getPerfil().getNombre())
                    .orElse(null);
            nuevoUsuario.setPerfil(perfil);
        } else {
            nuevoUsuario.setPerfil(usuarioExistente.getPerfil());
        }

        return nuevoUsuario;
    }

    /**
     * Migra todas las finanzas e informes del usuario antiguo al nuevo usuario.
     */
    private void migrateRelatedData(String oldUsername, Usuario usuarioAntiguo, Usuario nuevoUsuario) {
        // Migrar Finanzas
        java.util.List<Finanza> finanzas = finanzaRepository.findByUsuarioUsername(oldUsername);
        finanzas.forEach(finanza -> {
            finanza.setUsuario(nuevoUsuario);
            finanzaRepository.save(finanza);
        });

        // Migrar Informes
        java.util.List<HistorialInforme> informes = historialInformeRepository.findByUsuario(usuarioAntiguo);
        informes.forEach(informe -> {
            informe.setUsuario(nuevoUsuario);
            historialInformeRepository.save(informe);
        });
    }

    /**
     * Valida que un username no esté ya en uso.
     */
    private void validateUsernameNotTaken(String username) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
    }

    /**
     * Valida que un email no esté ya en uso.
     */
    private void validateEmailNotTaken(String email) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya existe");
        }
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public void deleteUsuario(String username) {
        if (!usuarioRepository.existsById(username)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario u = usuarioRepository.findById(username).get();

        java.util.List<Finanza> finanzas = finanzaRepository.findByUsuarioUsername(username);
        finanzaRepository.deleteAll(finanzas);

        java.util.List<HistorialInforme> informes = historialInformeRepository
                .findByUsuario(u);
        historialInformeRepository.deleteAll(informes);

        usuarioRepository.deleteById(username);
    }
}
