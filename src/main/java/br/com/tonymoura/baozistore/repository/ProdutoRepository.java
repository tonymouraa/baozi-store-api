package br.com.tonymoura.baozistore.repository;

import br.com.tonymoura.baozistore.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
