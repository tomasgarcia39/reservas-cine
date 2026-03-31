package com.tomi.reservas_cine;

import com.tomi.reservas_cine.dto.ReservaRequestDTO;
import com.tomi.reservas_cine.exception.AppException;
import com.tomi.reservas_cine.exception.ErrorCode;
import com.tomi.reservas_cine.model.*;
import com.tomi.reservas_cine.repository.AsientoRepository;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.ReservaRepository;
import com.tomi.reservas_cine.repository.SalaRepository;
import com.tomi.reservas_cine.service.AsientoService;
import com.tomi.reservas_cine.service.FuncionService;
import com.tomi.reservas_cine.service.ReservaService;
import com.tomi.reservas_cine.service.SalaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservasCineApplicationTests {

	@Mock
	private ReservaRepository reservaRepository;
	@Mock
	private AsientoRepository asientoRepository;
	@Mock
	private FuncionRepository funcionRepository;
	@Mock
	private SalaRepository salaRepository;
	@Mock
	private AsientoService asientoService;

	@InjectMocks
	private ReservaService reservaService;
	@InjectMocks
	private SalaService salaService;
	@InjectMocks
	private FuncionService funcionService;

	private ReservaRequestDTO dto;
	private Funcion funcion;
	private Asiento asiento;

	@BeforeEach
	void setUp() {
		Sala sala = new Sala("Sala 1", 10);
		funcion = new Funcion("Inception", LocalDateTime.now(), sala, 120);
		asiento = new Asiento(1, "ESTANDAR", sala);
		dto = new ReservaRequestDTO("tomi", 1L, 1L);
	}

	@Test
	void reservar_cuandoFuncionNoExiste_lanzaExcepcion() {
		when(funcionRepository.findById(1L)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> reservaService.reservar(dto));
		assertEquals(ErrorCode.FUNCION_NO_ENCONTRADA, ex.getErrorCode());
	}

	@Test
	void reservar_cuandoAsientoNoExiste_lanzaExcepcion() {
		when(funcionRepository.findById(1L)).thenReturn(Optional.of(funcion));
		when(asientoRepository.findById(1L)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> reservaService.reservar(dto));
		assertEquals(ErrorCode.ASIENTO_NO_ENCONTRADO, ex.getErrorCode());
	}

	@Test
	void reservar_cuandoAsientoConfirmado_lanzaExcepcion() {
		asiento.setEstado(EstadoAsiento.CONFIRMADO);
		when(funcionRepository.findById(1L)).thenReturn(Optional.of(funcion));
		when(asientoRepository.findById(1L)).thenReturn(Optional.of(asiento));

		AppException ex = assertThrows(AppException.class, () -> reservaService.reservar(dto));
		assertEquals(ErrorCode.ASIENTO_NO_DISPONIBLE, ex.getErrorCode());
	}

	@Test
	void reservar_cuandoAsientoReservadoTempVigente_lanzaExcepcion() {
		asiento.setEstado(EstadoAsiento.RESERVADO_TEMP);
		asiento.setExpiracion(LocalDateTime.now().plusMinutes(5));
		when(funcionRepository.findById(1L)).thenReturn(Optional.of(funcion));
		when(asientoRepository.findById(1L)).thenReturn(Optional.of(asiento));

		AppException ex = assertThrows(AppException.class, () -> reservaService.reservar(dto));
		assertEquals(ErrorCode.ASIENTO_NO_DISPONIBLE_TEMP, ex.getErrorCode());
	}

	@Test
	void reservar_cuandoTodoEstaOk_creaReserva() {
		Reserva reservaMock = new Reserva("tomi", funcion, asiento);
		when(funcionRepository.findById(1L)).thenReturn(Optional.of(funcion));
		when(asientoRepository.findById(1L)).thenReturn(Optional.of(asiento));
		when(asientoRepository.save(any())).thenReturn(asiento);
		when(reservaRepository.save(any())).thenReturn(reservaMock);

		assertDoesNotThrow(() -> reservaService.reservar(dto));
		assertEquals(EstadoAsiento.RESERVADO_TEMP, asiento.getEstado());
		verify(reservaRepository).save(any());
	}

	@Test
	void confirmarReserva_cuandoReservaNoExiste_lanzaExcepcion() {
		when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class, () -> reservaService.confirmarReserva(1L));
		assertEquals(ErrorCode.RESERVA_NO_ENCONTRADA, ex.getErrorCode());
	}

	@Test
	void confirmarReserva_cuandoAsientoExpirado_liberaAsientoYLanzaExcepcion() {
		asiento.setEstado(EstadoAsiento.RESERVADO_TEMP);
		asiento.setExpiracion(LocalDateTime.now().minusMinutes(5));
		Reserva reserva = new Reserva("tomi", funcion, asiento);
		when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

		AppException ex = assertThrows(AppException.class, () -> reservaService.confirmarReserva(1L));
		assertEquals(ErrorCode.ASIENTO_NO_DISPONIBLE_TEMP, ex.getErrorCode());
		assertEquals(EstadoAsiento.DISPONIBLE, asiento.getEstado());
	}

	@Test
	void confirmarReserva_cuandoTodoOk_confirmaAsiento() {
		asiento.setEstado(EstadoAsiento.RESERVADO_TEMP);
		asiento.setExpiracion(LocalDateTime.now().plusMinutes(5));
		Reserva reserva = new Reserva("tomi", funcion, asiento);
		when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
		when(asientoRepository.save(any())).thenReturn(asiento);

		assertDoesNotThrow(() -> reservaService.confirmarReserva(1L));
		assertEquals(EstadoAsiento.CONFIRMADO, asiento.getEstado());
	}

	@Test
	void eliminarSala_cuandoNoExiste_lanzaExcepcion() {
		when(salaRepository.existsById(1L)).thenReturn(false);

		AppException ex = assertThrows(AppException.class, () -> salaService.eliminarSala(1L));
		assertEquals(ErrorCode.SALA_NO_ENCONTRADA, ex.getErrorCode());
	}

	@Test
	void eliminarSala_cuandoExiste_eliminaCorrectamente() {
		when(salaRepository.existsById(1L)).thenReturn(true);

		assertDoesNotThrow(() -> salaService.eliminarSala(1L));
		verify(salaRepository).deleteById(1L);
	}

	@Test
	void guardarSala_creaAsientosAutomaticamente() {
		Sala sala = new Sala("Sala 1", 3);
		when(salaRepository.save(any())).thenReturn(sala);

		assertDoesNotThrow(() -> salaService.guardarSala(sala));
		verify(asientoService).generarAsientos(sala);
	}

	@Test
	void crearFuncion_cuandoSalaNoExiste_lanzaExcepcion() {
		when(salaRepository.findById(1L)).thenReturn(Optional.empty());

		AppException ex = assertThrows(AppException.class,
				() -> funcionService.crearFuncion(1L, "Inception", "2026-03-30T20:00:00", 120));
		assertEquals(ErrorCode.SALA_NO_ENCONTRADA, ex.getErrorCode());
	}

	@Test
	void crearFuncion_cuandoHaySolapamiento_lanzaExcepcion() {
		Sala sala = new Sala("Sala 1", 10);
		when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
		when(funcionRepository.existsSolapamiento(any(), any())).thenReturn(true);

		AppException ex = assertThrows(AppException.class,
				() -> funcionService.crearFuncion(1L, "Inception", "2026-03-30T20:00:00", 120));
		assertEquals(ErrorCode.FUNCION_DUPLICADA, ex.getErrorCode());
	}

	@Test
	void crearFuncion_cuandoTodoOk_creaFuncion() {
		Sala sala = new Sala("Sala 1", 10);
		when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
		when(funcionRepository.existsSolapamiento(any(), any())).thenReturn(false);
		when(funcionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		assertDoesNotThrow(() -> funcionService.crearFuncion(1L, "Inception", "2026-03-30T20:00:00", 120));
		verify(funcionRepository).save(any());
	}

	@Test
	void obtenerAsientosPorSala_devuelveListaCorrectamente() {
		Sala sala = new Sala("Sala 1", 10);
		Asiento a1 = new Asiento(1, "ESTANDAR", sala);
		Asiento a2 = new Asiento(2, "ESTANDAR", sala);
		when(asientoService.obtenerAsientosPorSala(1L)).thenReturn(List.of(a1, a2));

		var resultado = asientoService.obtenerAsientosPorSala(1L);

		assertEquals(2, resultado.size());
	}
}