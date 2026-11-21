package br.com.tonymoura.baozistore;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
    @Autowired TestRestTemplate http;

    @Test
    void verificaFluxoCompletoValidacaoEIntegridadeRelacional() {
        var cliente = post("/clientes", Map.of("nome", "Tony5230010", "clienteDesde", "2026-11-20"));
        assertThat(cliente.getStatusCode().value()).isEqualTo(201);
        long clienteId = cliente.getBody().get("id").asLong();
        assertThat(cliente.getHeaders().getLocation().getPath()).isEqualTo("/clientes/" + clienteId);

        var produto = post("/produtos", Map.of("nome", "BaoEspecial", "preco", 8.50, "estoque", true));
        assertThat(produto.getStatusCode().value()).isEqualTo(201);
        long produtoId = produto.getBody().get("id").asLong();

        var payload = Map.of("clienteId", clienteId, "produtoId", produtoId, "quantidade", 25);
        var pedido = post("/pedidos", payload);
        assertThat(pedido.getStatusCode().value()).isEqualTo(201);
        long pedidoId = pedido.getBody().get("id").asLong();
        assertThat(pedido.getBody().get("quantidade").asInt()).isEqualTo(25);

        for (var entry : Map.of("clientes", clienteId, "produtos", produtoId, "pedidos", pedidoId).entrySet()) {
            var lista = http.getForEntity("/" + entry.getKey(), JsonNode.class);
            assertThat(lista.getStatusCode().value()).isEqualTo(200);
            assertThat(lista.getBody().size()).isEqualTo(1);
            var porId = http.getForEntity("/" + entry.getKey() + "/" + entry.getValue(), JsonNode.class);
            assertThat(porId.getStatusCode().value()).isEqualTo(200);
            assertThat(porId.getBody().get("id").asLong()).isEqualTo(entry.getValue());
        }
        assertThat(http.getForObject("/clientes/" + clienteId, JsonNode.class).get("nome").asText()).isEqualTo("Tony5230010");
        assertThat(http.getForObject("/produtos/" + produtoId, JsonNode.class).get("nome").asText()).isEqualTo("BaoEspecial");
        assertThat(post("/pedidos", Map.of("clienteId", clienteId, "produtoId", produtoId, "quantidade", 0)).getStatusCode().value()).isEqualTo(400);
        assertThat(post("/produtos", Map.of("nome", "Inválido", "preco", -1, "estoque", true)).getStatusCode().value()).isEqualTo(400);
        assertThat(post("/clientes", Map.of("nome", " ", "clienteDesde", "2026-11-20")).getStatusCode().value()).isEqualTo(400);
        assertThat(post("/pedidos", Map.of("clienteId", 999999, "produtoId", produtoId, "quantidade", 25)).getStatusCode().value()).isEqualTo(404);
        assertThat(post("/pedidos", Map.of("clienteId", clienteId, "produtoId", 999999, "quantidade", 25)).getStatusCode().value()).isEqualTo(404);

        var indisponivel = post("/produtos", Map.of("nome", "Sem estoque", "preco", 8.50, "estoque", false));
        long indisponivelId = indisponivel.getBody().get("id").asLong();
        assertThat(post("/pedidos", Map.of("clienteId", clienteId, "produtoId", indisponivelId, "quantidade", 25)).getStatusCode().value()).isEqualTo(409);
        assertThat(delete("/clientes/" + clienteId)).isEqualTo(409);
        assertThat(delete("/produtos/" + produtoId)).isEqualTo(409);
        assertThat(http.getForEntity("/clientes/" + clienteId, JsonNode.class).getStatusCode().value()).isEqualTo(200);
        assertThat(http.getForEntity("/produtos/" + produtoId, JsonNode.class).getStatusCode().value()).isEqualTo(200);

        assertThat(delete("/pedidos/" + pedidoId)).isEqualTo(204);
        assertThat(delete("/produtos/" + produtoId)).isEqualTo(204);
        assertThat(delete("/clientes/" + clienteId)).isEqualTo(204);
        assertThat(delete("/produtos/" + indisponivelId)).isEqualTo(204);
        assertThat(http.getForEntity("/pedidos/" + pedidoId, JsonNode.class).getStatusCode().value()).isEqualTo(404);
        assertThat(delete("/pedidos/" + pedidoId)).isEqualTo(404);
        for (String entidade : new String[]{"clientes", "produtos", "pedidos"}) {
            assertThat(http.getForObject("/" + entidade, JsonNode.class).size()).isZero();
        }
    }

    private ResponseEntity<JsonNode> post(String path, Map<String, ?> value) {
        return http.postForEntity(path, value, JsonNode.class);
    }

    private int delete(String path) {
        return http.exchange(path, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class).getStatusCode().value();
    }
}
