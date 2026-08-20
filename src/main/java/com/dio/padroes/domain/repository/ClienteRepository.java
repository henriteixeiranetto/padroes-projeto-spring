package com.dio.padroes.domain.repository;

import com.dio.padroes.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Cliente> findByNomeContainingIgnoreCaseOrderByNomeAsc(String trecho);

    List<Cliente> findAllByOrderByNomeAsc();
}
