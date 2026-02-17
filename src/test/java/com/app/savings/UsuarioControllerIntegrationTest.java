package com.app.savings;

import com.app.savings.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback after each test to keep DB clean
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Limpiar usuario de prueba si existe (aunque @Transactional debería manejarlo)
        if (usuarioRepository.existsById("integrationUser")) {
            usuarioRepository.deleteById("integrationUser");
        }
    }

    @Test
    void testRegistroExitoso() throws Exception {
        // Given
        Map<String, Object> nuevoUsuario = new HashMap<>();
        nuevoUsuario.put("username", "integrationUser");
        nuevoUsuario.put("email", "integration@test.com");
        nuevoUsuario.put("password", "password123");

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nombre", "USER");
        nuevoUsuario.put("perfil", perfil);

        // When & Then
        mockMvc.perform(post("/api/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationUser"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.perfil.nombre").value("USER"));

        // Verificación en base de datos
        assertTrue(usuarioRepository.findByUsername("integrationUser").isPresent());
    }

    @Test
    void testLoginExitoso() throws Exception {
        // Pre-requisito: Registrar usuario primero
        testRegistroExitoso();

        // Given
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "integrationUser");
        credenciales.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationUser"))
                .andExpect(jsonPath("$.email").value("integration@test.com"));
    }

    @Test
    void testLoginFallido() throws Exception {
        // Given
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "noexiste");
        credenciales.put("password", "wrongpassword");

        // When & Then
        // Dependiendo de cómo esté implementado el controlador, podría devolver 401,
        // 404 o un body vacío/null con 200
        // Basado en el código actual, parece que devuelve null/vacío con 200 OK o error
        mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist()); // Espera respuesta vacía
    }
}
