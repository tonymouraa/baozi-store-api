package br.com.tonymoura.baozistore.repository;

import br.com.tonymoura.baozistore.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
