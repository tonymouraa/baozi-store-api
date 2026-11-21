package br.com.tonymoura.baozistore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "O id é gerado automaticamente")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "Informe a disponibilidade em estoque")
    @Column(nullable = false)
    private Boolean estoque;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Boolean getEstoque() { return estoque; }
    public void setEstoque(Boolean estoque) { this.estoque = estoque; }

}
