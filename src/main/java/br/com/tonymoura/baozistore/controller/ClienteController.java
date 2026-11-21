package br.com.tonymoura.baozistore.controller;

import br.com.tonymoura.baozistore.model.Cliente;
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
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente value) {
        Cliente saved = repository.saveAndFlush(value);
        return ResponseEntity.created(URI.create("/clientes/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Cliente consultar(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Cliente value = consultar(id);
        repository.delete(value);
        repository.flush();
        return ResponseEntity.noContent().build();
    }
}
