# Baozi Store API

Atividade prática de Desenvolvimento Web Back-End de **Tony Moura**.

API REST para cadastrar clientes e produtos e registrar pedidos de um único produto. O cenário de demonstração utiliza o cliente **Tony5230010**, o produto **BaoEspecial**, preço fictício de **R$ 8,50**, e um pedido de **25 unidades**. O campo `estoque` indica disponibilidade (`true` ou `false`); ele não representa uma quantidade de unidades.

## Tecnologias

- Java 21
- Spring Boot 3.5.16
- Spring Data JPA e Bean Validation
- Banco relacional H2 em arquivo
- Maven 3.6.3 ou superior
- JSON nas requisições e respostas

## Executar

Com Java 21 e Maven instalados:

```sh
mvn clean test package
java -jar target/baozi-store-api-1.0.0.jar
```

A API fica disponível em `http://localhost:8080`. Alternativamente, execute `mvn spring-boot:run`.

O banco é criado automaticamente em `data/baozi.mv.db`, relativo ao diretório de execução. Não há registros pré-cadastrados. Os dados são mantidos entre reinicializações. Para começar uma demonstração vazia, pare a aplicação e remova apenas a pasta local `data/` deste projeto.

## Organização

```text
src/main/java/br/com/tonymoura/baozistore/
├── BaoziStoreApplication.java
├── model/          entidades JPA
├── repository/     interfaces Spring Data JPA
└── controller/     endpoints MVC e tratamento de erros
src/main/resources/
├── application.properties
└── schema.sql      tabelas, chaves primárias e chaves estrangeiras
src/test/           teste integrado com banco H2 isolado em memória
postman/            coleção JSON importável distribuída no ZIP
docs/               diagrama de casos de uso em PlantUML
```

As entidades mantêm exatamente os atributos sugeridos no enunciado:

| Entidade | Atributos Java |
| --- | --- |
| Cliente | `id: Long`, `nome: String`, `clienteDesde: LocalDate` |
| Produto | `id: Long`, `nome: String`, `preco: BigDecimal`, `estoque: Boolean` |
| Pedido | `id: Long`, `clienteId: Long`, `produtoId: Long`, `quantidade: Integer` |

`Pedido.clienteId` e `Pedido.produtoId` são chaves estrangeiras no banco. Cada pedido referencia um cliente e um produto; cada cliente e produto pode estar relacionado a vários pedidos. O esquema usa nomes de coluna em `snake_case`, preservando os nomes de atributos Java e JSON solicitados.

## Endpoints

Todos os caminhos abaixo são relativos a `http://localhost:8080`.

| Método | Caminho | Resultado |
| --- | --- | --- |
| POST | `/clientes` | Cria cliente; 201 e cabeçalho Location |
| GET | `/clientes` | Lista clientes; 200 |
| GET | `/clientes/{id}` | Consulta cliente; 200 |
| DELETE | `/clientes/{id}` | Exclui cliente; 204 |
| POST | `/produtos` | Cria produto; 201 e cabeçalho Location |
| GET | `/produtos` | Lista produtos; 200 |
| GET | `/produtos/{id}` | Consulta produto; 200 |
| DELETE | `/produtos/{id}` | Exclui produto; 204 |
| POST | `/pedidos` | Cria pedido; 201 e cabeçalho Location |
| GET | `/pedidos` | Lista pedidos; 200 |
| GET | `/pedidos/{id}` | Consulta pedido; 200 |
| DELETE | `/pedidos/{id}` | Exclui pedido; 204 |

O identificador é gerado automaticamente e deve ser omitido no POST. Não foram implementados endpoints PUT, que são opcionais no enunciado.

### Dados do cenário

`POST /clientes`:

```json
{
  "nome": "Tony5230010",
  "clienteDesde": "2026-11-20"
}
```

`POST /produtos`:

```json
{
  "nome": "BaoEspecial",
  "preco": 8.50,
  "estoque": true
}
```

`POST /pedidos`, substituindo os identificadores pelos retornados nos POSTs anteriores:

```json
{
  "clienteId": 1,
  "produtoId": 1,
  "quantidade": 25
}
```

### Validações e respostas de erro

- `400`: JSON inválido, campos obrigatórios ausentes, nome vazio, data de cadastro futura, preço não positivo ou com mais de duas casas decimais, quantidade não positiva ou ID informado no POST.
- `404`: recurso não encontrado ou cliente/produto inexistente ao criar um pedido.
- `409`: produto indisponível para criação de pedido, ou tentativa de excluir cliente/produto que ainda tem pedidos vinculados.

Exemplo de resposta:

```json
{
  "status": 404,
  "mensagem": "Cliente não encontrado"
}
```

Para excluir os dados do cenário, exclua primeiro o pedido, depois o produto e o cliente. Não há exclusão em cascata. O modelo não controla saldo de estoque, pagamento ou entrega, pois esses atributos não fazem parte do escopo solicitado.

## Testes no Postman

1. Extraia o pacote do projeto e importe `postman/Baozi_Store.postman_collection.json` no Postman. A mesma coleção padrão JSON é fornecida separadamente em `output/Baozi_Store.postman_collection.json` no diretório de entrega. No Postman 12, a importação pode converter a coleção para arquivos YAML locais; o JSON distribuído permanece como arquivo de intercâmbio.
2. Inicie a API e confira a variável de coleção `baseUrl` com valor `http://localhost:8080`.
3. Execute as 12 requisições na ordem numérica. Os POSTs salvam os IDs reais nas variáveis `clienteId`, `produtoId` e `pedidoId`.
4. Capture as telas dos POSTs de cada entidade, dos três GETs gerais, dos três GETs por ID e dos três DELETEs, mantendo método, URL, corpo quando aplicável e status da resposta visíveis.
5. Os DELETEs retornam `204 No Content`, portanto não possuem corpo de resposta.

A coleção inclui testes de status HTTP e da quantidade de 25 unidades no pedido. Os dados do cenário aparecem nos POSTs e nos GETs, conforme exige o enunciado. Execute novamente os POSTs para recriar os registros se a coleção já tiver concluído as exclusões.

## Teste automatizado

`mvn test` executa um teste integrado HTTP com aplicação real em porta aleatória e H2 em memória. Ele cobre criação das três entidades, listagem e consulta por ID, exclusão, quantidade e preço inválidos, nome vazio, referências inexistentes, produto indisponível e bloqueio da exclusão de registros referenciados. O teste também confirma que uma exclusão bloqueada preserva os registros.

## Referências técnicas

- [Requisitos do Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [Banco H2](https://h2database.com/html/main.html)
