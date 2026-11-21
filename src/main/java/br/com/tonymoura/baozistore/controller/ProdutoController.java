package br.com.tonymoura.baozistore.controller;

import br.com.tonymoura.baozistore.model.Produto;
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
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto value) {
        Produto saved = repository.saveAndFlush(value);
        return ResponseEntity.created(URI.create("/produtos/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Produto> listar() {
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Produto consultar(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Produto value = consultar(id);
        repository.delete(value);
        repository.flush();
        return ResponseEntity.noContent().build();
    }
}
