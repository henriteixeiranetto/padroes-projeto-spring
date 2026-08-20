package com.dio.padroes.pattern.chain;

import com.dio.padroes.api.exception.RegraDeNegocioException;
import com.dio.padroes.domain.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CadeiaDeValidacaoClienteTest {

    private ClienteRepository clienteRepository;
    private CadeiaDeValidacaoCliente cadeia;

    @BeforeEach
    void montar() {
        clienteRepository = Mockito.mock(ClienteRepository.class);
        cadeia = new CadeiaDeValidacaoCliente(List.of(
                new NomeHandler(),
                new EmailHandler(clienteRepository),
                new TelefoneHandler(),
                new CepHandler()));
        cadeia.montarCadeia();
    }

    @Test
    @DisplayName("normaliza nome, e-mail, telefone e CEP quando os dados sao validos")
    void deveNormalizarDados() {
        DadosClienteValidados dados = cadeia.validar(
                "  Maria    Silva ", "MARIA.Silva@Empresa.com.BR ", "11987654321", "01001000", null);

        assertThat(dados.nome()).isEqualTo("Maria Silva");
        assertThat(dados.email()).isEqualTo("maria.silva@empresa.com.br");
        assertThat(dados.telefone()).isEqualTo("(11) 98765-4321");
        assertThat(dados.cep()).isEqualTo("01001-000");
    }

    @Test
    @DisplayName("percorre todos os elos, na ordem em que foram encadeados")
    void devePercorrerTodosOsElos() {
        DadosClienteValidados dados = cadeia.validar(
                "Ana Souza", "ana@empresa.com", null, "90010-150", null);

        assertThat(dados.trilha())
                .containsExactly("NomeHandler", "EmailHandler", "TelefoneHandler", "CepHandler");
    }

    @Test
    @DisplayName("acumula todos os problemas em vez de parar no primeiro")
    void deveAcumularErros() {
        assertThatThrownBy(() -> cadeia.validar("Al", "email-invalido", "123", "123", null))
                .isInstanceOf(RegraDeNegocioException.class)
                .satisfies(erro -> assertThat(((RegraDeNegocioException) erro).getErros())
                        .hasSize(4)
                        .anyMatch(mensagem -> mensagem.startsWith("nome:"))
                        .anyMatch(mensagem -> mensagem.startsWith("email:"))
                        .anyMatch(mensagem -> mensagem.startsWith("telefone:"))
                        .anyMatch(mensagem -> mensagem.startsWith("cep:")));
    }

    @Test
    @DisplayName("recusa e-mail ja cadastrado por outro cliente")
    void deveRecusarEmailDuplicado() {
        Mockito.when(clienteRepository.existsByEmailIgnoreCase("maria@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> cadeia.validar("Maria Silva", "maria@empresa.com", null, "01001-000", null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Não foi possível salvar o cliente");
    }

    @Test
    @DisplayName("permite que o cliente em edicao mantenha o proprio e-mail")
    void devePermitirMesmoEmailNaEdicao() {
        Mockito.when(clienteRepository.existsByEmailIgnoreCaseAndIdNot("maria@empresa.com", 7L)).thenReturn(false);

        DadosClienteValidados dados = cadeia.validar(
                "Maria Silva", "maria@empresa.com", null, "01001-000", 7L);

        assertThat(dados.email()).isEqualTo("maria@empresa.com");
        Mockito.verify(clienteRepository).existsByEmailIgnoreCaseAndIdNot("maria@empresa.com", 7L);
        Mockito.verify(clienteRepository, Mockito.never()).existsByEmailIgnoreCase(Mockito.anyString());
    }
}
