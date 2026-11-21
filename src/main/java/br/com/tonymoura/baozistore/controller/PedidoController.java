package br.com.tonymoura.baozistore.controller;

import br.com.tonymoura.baozistore.model.Pedido;
import br.com.tonymoura.baozistore.repository.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoRepository repository;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoController(PedidoRepository repository, ClienteRepository clientes, ProdutoRepository produtos) {
        this.repository = repository;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@Valid @RequestBody Pedido value) {
        if (!clientes.existsById(value.getClienteId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        var produto = produtos.findById(value.getProdutoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        if (!Boolean.TRUE.equals(produto.getEstoque())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produto indisponível em estoque");
        }
        Pedido saved = repository.saveAndFlush(value);
        return ResponseEntity.created(URI.create("/pedidos/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Pedido> listar() {
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Pedido consultar(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Pedido value = consultar(id);
        repository.delete(value);
        repository.flush();
        return ResponseEntity.noContent().build();
    }
}
