package br.com.tonymoura.baozistore.repository;

import br.com.tonymoura.baozistore.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
