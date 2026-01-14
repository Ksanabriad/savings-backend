package com.app.savings.services;

import com.app.savings.entities.Usuario;
import com.app.savings.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Login simple devolviendo el usuario si coincide la contraseña, null si no
    public Usuario login(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public Usuario register(Usuario usuario) {
        // Por defecto rol USER si no viene especificado
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }
        return usuarioRepository.save(usuario);
    }

    public void initAdmin() {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password("admin1234")
                    .email("admin@easysave.com")
                    .rol("ADMIN")
                    .build();
            usuarioRepository.save(admin);
            System.out.println("Usuario ADMIN creado por defecto");
        }
    }
}
