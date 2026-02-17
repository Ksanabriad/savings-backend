package com.app.savings.services;

import com.app.savings.entities.*;
import com.app.savings.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UsuarioService.
 * Valida la funcionalidad de login, registro, actualización y eliminación de
 * usuarios,
 * incluyendo los métodos refactorizados para mejor adherencia a SOLID.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private FinanzaRepository finanzaRepository;

    @Mock
    private HistorialInformeRepository historialInformeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario testUsuario;
    private PerfilUsuario perfilUser;
    private PerfilUsuario perfilAdmin;

    @BeforeEach
    void setUp() {
        // Configurar perfiles
        perfilUser = new PerfilUsuario();
        perfilUser.setId(1L);
        perfilUser.setNombre("USER");

        perfilAdmin = new PerfilUsuario();
        perfilAdmin.setId(2L);
        perfilAdmin.setNombre("ADMIN");

        // Usuario de prueba
        testUsuario = Usuario.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .perfil(perfilUser)
                .build();
    }

    // ==================== TESTS DE LOGIN ====================

    @Test
    void testLoginExitoso() {
        // Given
        when(usuarioRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        // When
        Usuario result = usuarioService.login("testuser", "password123");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(usuarioRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void testLoginPasswordIncorrecta() {
        // Given
        when(usuarioRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUsuario));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword"))
                .thenReturn(false);

        // When
        Usuario result = usuarioService.login("testuser", "wrongpassword");

        // Then
        assertNull(result);
        verify(usuarioRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void testLoginUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findByUsernameOrEmail("noexiste", "noexiste"))
                .thenReturn(Optional.empty());

        // When
        Usuario result = usuarioService.login("noexiste", "password123");

        // Then
        assertNull(result);
        verify(usuarioRepository).findByUsernameOrEmail("noexiste", "noexiste");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ==================== TESTS DE REGISTER ====================

    @Test
    void testRegisterExitoso() {
        // Given
        Usuario nuevoUsuario = Usuario.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .perfil(perfilUser)
                .build();

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(perfilUsuarioRepository.findByNombre("USER")).thenReturn(Optional.of(perfilUser));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.register(nuevoUsuario);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("encodedPassword123", result.getPassword());
        verify(usuarioRepository).findByUsername("newuser");
        verify(usuarioRepository).findByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testRegisterUsernameDuplicado() {
        // Given
        Usuario nuevoUsuario = Usuario.builder()
                .username("testuser")
                .email("newemail@example.com")
                .password("password123")
                .build();

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(testUsuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.register(nuevoUsuario);
        });

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegisterEmailDuplicado() {
        // Given
        Usuario nuevoUsuario = Usuario.builder()
                .username("newuser")
                .email("test@example.com")
                .password("password123")
                .build();

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUsuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.register(nuevoUsuario);
        });

        assertEquals("El email ya existe", exception.getMessage());
        verify(usuarioRepository).findByEmail("test@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==================== TESTS DE UPDATE ====================

    @Test
    void testUpdateUsuarioSinCambioUsername() {
        // Given
        Usuario usuarioActualizado = Usuario.builder()
                .username("testuser") // Mismo username
                .email("newemail@example.com")
                .password("newpassword")
                .perfil(perfilAdmin)
                .build();

        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(perfilUsuarioRepository.findByNombre("ADMIN")).thenReturn(Optional.of(perfilAdmin));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.updateUsuario("testuser", usuarioActualizado);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("encodedNewPassword", result.getPassword());
        assertEquals(perfilAdmin, result.getPerfil());
        verify(usuarioRepository).save(testUsuario);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void testUpdateUsuarioConCambioUsername() {
        // Given
        Usuario usuarioActualizado = Usuario.builder()
                .username("newusername") // Username cambiado
                .email("test@example.com")
                .password("password123")
                .perfil(perfilUser)
                .build();

        List<Finanza> finanzas = new ArrayList<>();
        Finanza finanza = new Finanza();
        finanza.setId(1L);
        finanza.setUsuario(testUsuario);
        finanzas.add(finanza);

        List<HistorialInforme> informes = new ArrayList<>();
        HistorialInforme informe = new HistorialInforme();
        informe.setId(1L);
        informe.setUsuario(testUsuario);
        informes.add(informe);

        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.findByUsername("newusername")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(perfilUsuarioRepository.findByNombre("USER")).thenReturn(Optional.of(perfilUser));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(finanzaRepository.findByUsuarioUsername("testuser")).thenReturn(finanzas);
        when(historialInformeRepository.findByUsuario(testUsuario)).thenReturn(informes);
        when(finanzaRepository.save(any(Finanza.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialInformeRepository.save(any(HistorialInforme.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.updateUsuario("testuser", usuarioActualizado);

        // Then
        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(usuarioRepository).findByUsername("newusername");
        verify(usuarioRepository).saveAndFlush(testUsuario); // Email temporal
        verify(usuarioRepository).save(any(Usuario.class)); // Nuevo usuario
        verify(finanzaRepository).findByUsuarioUsername("testuser");
        verify(finanzaRepository).save(any(Finanza.class)); // Migración
        verify(historialInformeRepository).findByUsuario(testUsuario);
        verify(historialInformeRepository).save(any(HistorialInforme.class)); // Migración
        verify(usuarioRepository).delete(testUsuario); // Elimina el antiguo
    }

    @Test
    void testUpdateUsuarioEmailDuplicado() {
        // Given
        Usuario otroUsuario = Usuario.builder()
                .username("otheruser")
                .email("other@example.com")
                .build();

        Usuario usuarioActualizado = Usuario.builder()
                .username("testuser")
                .email("other@example.com") // Email ya existe
                .build();

        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otroUsuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario("testuser", usuarioActualizado);
        });

        assertEquals("El email ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testUpdateUsuarioNewUsernameDuplicado() {
        // Given
        Usuario otroUsuario = Usuario.builder()
                .username("existinguser")
                .email("existing@example.com")
                .build();

        Usuario usuarioActualizado = Usuario.builder()
                .username("existinguser") // Username ya existe
                .email("test@example.com")
                .build();

        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.findByUsername("existinguser")).thenReturn(Optional.of(otroUsuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario("testuser", usuarioActualizado);
        });

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testUpdateUsuarioNoExiste() {
        // Given
        Usuario usuarioActualizado = Usuario.builder()
                .username("noexiste")
                .email("test@example.com")
                .build();

        when(usuarioRepository.findById("noexiste")).thenReturn(Optional.empty());

        // When
        Usuario result = usuarioService.updateUsuario("noexiste", usuarioActualizado);

        // Then
        assertNull(result);
        verify(usuarioRepository).findById("noexiste");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ==================== TESTS DE DELETE ====================

    @Test
    void testDeleteUsuarioExitoso() {
        // Given
        List<Finanza> finanzas = new ArrayList<>();
        List<HistorialInforme> informes = new ArrayList<>();

        when(usuarioRepository.existsById("testuser")).thenReturn(true);
        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));
        when(finanzaRepository.findByUsuarioUsername("testuser")).thenReturn(finanzas);
        when(historialInformeRepository.findByUsuario(testUsuario)).thenReturn(informes);

        // When
        usuarioService.deleteUsuario("testuser");

        // Then
        verify(usuarioRepository).existsById("testuser");
        verify(finanzaRepository).findByUsuarioUsername("testuser");
        verify(historialInformeRepository).findByUsuario(testUsuario);
        verify(finanzaRepository).deleteAll(finanzas);
        verify(historialInformeRepository).deleteAll(informes);
        verify(usuarioRepository).deleteById("testuser");
    }

    @Test
    void testDeleteUsuarioNoExiste() {
        // Given
        when(usuarioRepository.existsById("noexiste")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.deleteUsuario("noexiste");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository).existsById("noexiste");
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    // ==================== TESTS ADICIONALES ====================

    @Test
    void testFindAll() {
        // Given
        List<Usuario> usuarios = List.of(testUsuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = usuarioService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(usuarioRepository.findById("testuser")).thenReturn(Optional.of(testUsuario));

        // When
        Usuario result = usuarioService.findById("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(usuarioRepository).findById("testuser");
    }

    @Test
    void testFindByUsername() {
        // Given
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(testUsuario));

        // When
        Optional<Usuario> result = usuarioService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(usuarioRepository).findByUsername("testuser");
    }
}
