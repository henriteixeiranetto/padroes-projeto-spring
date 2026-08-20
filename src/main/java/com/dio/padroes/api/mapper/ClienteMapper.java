package com.dio.padroes.api.mapper;

import com.dio.padroes.api.dto.ClienteRequest;
import com.dio.padroes.api.dto.ClienteResponse;
import com.dio.padroes.api.dto.EnderecoResponse;
import com.dio.padroes.domain.model.Cliente;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.service.ClienteForm;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteForm toForm(ClienteRequest request) {
        return new ClienteForm(request.nome(), request.email(), request.telefone(), request.cep());
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                toResponse(cliente.getEndereco()),
                cliente.getCriadoEm(),
                cliente.getAtualizadoEm());
    }

    public static EnderecoResponse toResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoResponse(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getLocalidade(),
                endereco.getUf(),
                endereco.getDdd(),
                endereco.getFonte());
    }
}
