# Padrões de projeto com Java e Spring

Projeto do desafio final do bootcamp da DIO sobre padrões de projeto. Parti do laboratório
[lab-padroes-projeto-spring](https://github.com/digitalinnovationone/lab-padroes-projeto-spring),
que demonstra Singleton, Strategy/Repository e Facade num CRUD de clientes integrado ao ViaCEP,
e fui acrescentando outros padrões conforme apareciam problemas que pediam por eles.

A API cadastra clientes (resolvendo o endereço pelo CEP) e simula frete por modalidade.
Tem uma tela simples em HTML/CSS/JS puro em `/` para não precisar de Postman para ver as coisas
acontecendo.

Stack: Java 21, Spring Boot 3.5, Spring Data JPA, H2 em memória, springdoc/Swagger.

## Rodando

```bash
./mvnw spring-boot:run
```

- Tela: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2: http://localhost:8080/h2-console (JDBC `jdbc:h2:mem:padroesdb`, usuário `sa`, sem senha)

Banco em memória e três clientes de exemplo já carregados, então não precisa configurar nada.
Funciona sem internet também: se o ViaCEP não responder, a consulta cai para uma tabela local
e o endereço volta marcado com `fonte: OFFLINE`.

## Padrões usados e por quê

- **Singleton** (`TabelaDeTarifas`): tabela de preços e prazos consultada em todo cálculo de frete.
  Não tem `getInstance()`: quem garante a instância única é o próprio contêiner do Spring.
- **Strategy** (`FreteStrategy` e as quatro modalidades): PAC, SEDEX, Expresso e Retirada calculam
  preço e prazo de formas diferentes. Cada uma numa classe, sem `switch` no meio do caminho.
- **Factory** (`FreteStrategyFactory`): recebe do Spring a lista de estratégias e monta o mapa
  modalidade → estratégia. Modalidade nova se registra sozinha.
- **Facade** (`ClienteFacade`): cadastrar cliente envolve validar, consultar CEP, gravar endereço,
  gravar cliente e publicar evento. O controller só chama `criar()`.
- **Chain of Responsibility** (`CadeiaDeValidacaoCliente` e os quatro elos): cada regra de cadastro
  numa classe. Regra nova é uma classe nova, nenhum arquivo existente muda.
- **Observer** (eventos + `AuditoriaListener`, `NotificacaoListener`): a facade publica o fato e
  quem se interessa reage. Foi o jeito de não deixar a facade crescer a cada nova necessidade.
- **Template Method** (`NotificacaoTemplate`): e-mail e SMS seguem o mesmo roteiro de envio;
  o SMS só muda a montagem do corpo, que corta em 140 caracteres.
- **Adapter** (`ViaCepAdapter`): traduz a resposta do ViaCEP para a entidade `Endereco`.
- **Decorator** (`CepServiceComCache`): embrulha o `ViaCepService` acrescentando cache e queda para
  o modo offline, sem tocar na classe da integração.
- **Builder** (`Cliente`, `Endereco`): oito campos quase todos opcionais no endereço; construtor
  telescópico ficaria ilegível.
- **Repository** e **DTO**: Spring Data nas consultas e records de entrada/saída com mapper, para
  não expor as entidades JPA na API.

Tem um endpoint `GET /api/padroes` que devolve esse mesmo catálogo em JSON, com o problema, a
solução e as classes de cada padrão. A tela inicial consome ele.

## Para ver funcionando

1. Cadastre um cliente com o CEP `01310-100`. O endereço vem preenchido pelo ViaCEP.
2. Tente cadastrar com nome `Al`, e-mail `abc` e CEP `123`. A resposta 422 traz os quatro erros
   de uma vez, porque a cadeia roda todos os elos antes de reclamar.
3. Consulte o mesmo CEP duas vezes: a etiqueta muda de `VIACEP` para `CACHE`.
4. Simule um frete para `90010-150` sem escolher modalidade. As quatro estratégias respondem juntas
   e cada linha mostra qual classe calculou.
5. Abra a aba Bastidores: eventos publicados, notificações geradas nos dois canais e as métricas
   do cache.

## Endpoints

| Método | Rota | |
|---|---|---|
| GET | `/api/clientes?nome=` | lista, com filtro opcional |
| GET | `/api/clientes/{id}` | busca por id |
| POST | `/api/clientes` | cadastra |
| PUT | `/api/clientes/{id}` | atualiza |
| DELETE | `/api/clientes/{id}` | remove |
| GET | `/api/cep/{cep}` | consulta endereço |
| POST | `/api/fretes/simular` | simula uma modalidade ou compara todas |
| GET | `/api/fretes/modalidades` | modalidades disponíveis |
| GET | `/api/padroes` | catálogo dos padrões |
| GET | `/api/padroes/eventos` | eventos observados |
| GET | `/api/padroes/notificacoes` | notificações geradas |

Erros seguem o formato ProblemDetail, com uma lista `erros` do que precisa ser corrigido:

```json
{
  "title": "Regra de negócio violada",
  "status": 422,
  "detail": "Não foi possível salvar o cliente",
  "erros": [
    "nome: precisa ter ao menos 3 caracteres",
    "email: formato inválido (ex.: maria@empresa.com.br)",
    "cep: precisa ter 8 dígitos (ex.: 01001-000)"
  ]
}
```

## Algumas decisões

**Bean Validation e cadeia de validação juntos.** As anotações do `ClienteRequest` cuidam do formato
da requisição e devolvem 400; a cadeia cuida das regras de negócio (e-mail duplicado, normalização
de telefone e CEP) e devolve 422. São coisas diferentes.

**A cadeia não para no primeiro erro.** A implementação clássica interrompe quando um elo assume a
responsabilidade. Aqui todos os elos rodam e acumulam os problemas, senão quem consome a API
descobre um erro por requisição, o que é irritante de usar.

**HTTP Interface no lugar do OpenFeign.** O laboratório usa `spring-cloud-starter-openfeign`.
Como o Spring 6 já traz interfaces declarativas com `@HttpExchange`, troquei e economizei o
Spring Cloud inteiro no `pom.xml`.

**Fallback offline ligado por padrão.** Precisava rodar em qualquer máquina, inclusive sem rede.
O dado do modo offline nunca se disfarça de oficial: vem com `fonte: OFFLINE`. Para desligar,
`app.via-cep.fallback-offline: false`. Aí a API responde 503 quando o ViaCEP cai.

## Testes

```bash
./mvnw test
```

23 testes, nenhum depende de internet. No teste de integração o ViaCEP é apontado para uma porta
fechada de propósito, o que ainda exercita o caminho de contingência do Decorator.

## Ideias para depois

- [ ] `@Async` nos listeners, para tirar as notificações do caminho da requisição
- [ ] perfil de produção com PostgreSQL e Flyway
- [ ] paginação na listagem de clientes
- [ ] circuit breaker (Resilience4j) junto do Decorator
