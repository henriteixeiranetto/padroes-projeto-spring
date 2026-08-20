package com.dio.padroes.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ViaCEP apontado para uma porta fechada: a suite roda sem internet e ainda exercita o fallback
@SpringBootTest(properties = {
        "app.via-cep.url=http://localhost:1",
        "app.via-cep.connect-timeout=200ms",
        "app.via-cep.read-timeout=200ms",
        "app.seed.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testesdb;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class ClienteApiTest {

    @Autowired
    private MockMvc mockMvc;

    private String json(String nome, String email, String telefone, String cep) {
        return """
                {"nome": "%s", "email": "%s", "telefone": "%s", "cep": "%s"}
                """.formatted(nome, email, telefone, cep);
    }

    @Test
    @DisplayName("cadastra o cliente, normaliza os dados e resolve o endereco pelo CEP")
    void deveCadastrarCliente() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("  Maria   Silva ", "MARIA@empresa.com.br", "11987654321", "01001000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@empresa.com.br"))
                .andExpect(jsonPath("$.telefone").value("(11) 98765-4321"))
                .andExpect(jsonPath("$.endereco.cep").value("01001-000"))
                .andExpect(jsonPath("$.endereco.fonte").value("OFFLINE"));
    }

    @Test
    @DisplayName("devolve 422 com todos os erros de negocio de uma vez")
    void deveReportarTodosOsErros() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Al", "sem-arroba", "123", "1")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erros", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.erros", hasItem(startsWith("email:"))))
                .andExpect(jsonPath("$.erros", hasItem(startsWith("cep:"))));
    }

    @Test
    @DisplayName("devolve 400 quando faltam campos obrigatorios da requisicao")
    void deveRecusarRequisicaoIncompleta() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("recusa e-mail duplicado")
    void deveRecusarEmailDuplicado() throws Exception {
        String corpo = json("João Pereira", "joao.duplicado@empresa.com", "21998765432", "20040-020");

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erros", hasItem(startsWith("email: já existe"))));
    }

    @Test
    @DisplayName("o cadastro dispara os observadores e as notificacoes dos dois canais")
    void deveDispararEventosENotificacoes() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana Souza", "ana.observer@empresa.com", "51999887766", "90010-150")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/padroes/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("CLIENTE_CRIADO"))
                .andExpect(jsonPath("$[0].observador").value("AuditoriaListener"));

        mockMvc.perform(get("/api/padroes/notificacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].canal", hasItem("EMAIL")))
                .andExpect(jsonPath("$[*].canal", hasItem("SMS")));
    }

    @Test
    @DisplayName("devolve 404 para cliente inexistente")
    void deveDevolver404() throws Exception {
        mockMvc.perform(get("/api/clientes/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    @DisplayName("simula todas as modalidades de frete em uma unica chamada")
    void deveSimularTodasAsModalidades() throws Exception {
        mockMvc.perform(post("/api/fretes/simular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cep": "90010-150", "pesoKg": 2.5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].modalidade").value("RETIRADA"))
                .andExpect(jsonPath("$[*].estrategia", everyItem(startsWith("Frete"))));
    }

    @Test
    @DisplayName("o catalogo de padroes e a cadeia de validacao ficam disponiveis na API")
    void deveExporOsPadroes() throws Exception {
        mockMvc.perform(get("/api/padroes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(12)))
                .andExpect(jsonPath("$[*].padrao", hasItem("Chain of Responsibility")));

        mockMvc.perform(get("/api/padroes/chain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].elo").value("NomeHandler"));

        mockMvc.perform(get("/api/padroes/singleton"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesmaInstancia").value(true));
    }
}
