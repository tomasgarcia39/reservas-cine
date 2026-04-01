package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.AuthResponseDTO;
import com.tomi.reservas_cine.dto.LoginRequestDTO;
import com.tomi.reservas_cine.dto.RegisterRequestDTO;
import com.tomi.reservas_cine.exception.AppException;
import com.tomi.reservas_cine.exception.ErrorCode;
import com.tomi.reservas_cine.model.Rol;
import com.tomi.reservas_cine.model.Usuario;
import com.tomi.reservas_cine.repository.UsuarioRepository;
import com.tomi.reservas_cine.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_YA_REGISTRADO);
        }

        Usuario usuario = new Usuario(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                Rol.ROLE_USER
        );

        usuarioRepository.save(usuario);
        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
        String refreshToken = jwtService.generarRefreshToken(usuario.getEmail());
        return new AuthResponseDTO(token, refreshToken, usuario.getEmail(), usuario.getRol().name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new AppException(ErrorCode.CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(dto.password(), usuario.getPassword())) {
            throw new AppException(ErrorCode.CREDENCIALES_INVALIDAS);
        }

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
        String refreshToken = jwtService.generarRefreshToken(usuario.getEmail());
        return new AuthResponseDTO(token, refreshToken, usuario.getEmail(), usuario.getRol().name());
    }

    public AuthResponseDTO refresh(String refreshToken) {
        String email = jwtService.extraerEmail(refreshToken);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.CREDENCIALES_INVALIDAS));

        if (!jwtService.esRefreshTokenValido(refreshToken, email)) {
            throw new AppException(ErrorCode.TOKEN_INVALIDO);
        }

        String nuevoToken = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
        String nuevoRefreshToken = jwtService.generarRefreshToken(usuario.getEmail());
        return new AuthResponseDTO(nuevoToken, nuevoRefreshToken, usuario.getEmail(), usuario.getRol().name());
    }
}