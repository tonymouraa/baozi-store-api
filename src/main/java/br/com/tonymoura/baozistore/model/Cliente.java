package br.com.tonymoura.baozistore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "O id é gerado automaticamente")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotNull(message = "A data de cadastro é obrigatória")
    @PastOrPresent(message = "A data de cadastro não pode ser futura")
    @Column(nullable = false)
    private LocalDate clienteDesde;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getClienteDesde() { return clienteDesde; }
    public void setClienteDesde(LocalDate clienteDesde) { this.clienteDesde = clienteDesde; }

}
