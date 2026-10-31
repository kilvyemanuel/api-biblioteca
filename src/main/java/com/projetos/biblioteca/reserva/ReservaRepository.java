package com.projetos.biblioteca.reserva;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByUsuarioIdAndLivroIdAndStatus(Long usuarioId, Long livroId, StatusReserva status);
    boolean existsByLivroIdAndStatusAndUsuarioIdNot(Long livroId, StatusReserva status, Long usuarioId);
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByLivroId(Long livroId);
    Reserva findByUsuarioIdAndLivroIdAndStatus(Long usuarioId, Long livroId, StatusReserva status);
}
