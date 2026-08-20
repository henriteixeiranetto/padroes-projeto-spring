package com.dio.padroes.service;

import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.support.Ceps;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service("cepOfflineService")
public class CepOfflineService implements CepService {

    public static final String FONTE = "OFFLINE";

    // faixas de CEP por UF, conforme a tabela dos Correios
    private static final Map<int[], String> FAIXAS_UF = new LinkedHashMap<>();

    private static final Map<String, Endereco> CONHECIDOS = new LinkedHashMap<>();

    static {
        FAIXAS_UF.put(new int[]{1000, 19999}, "SP");
        FAIXAS_UF.put(new int[]{20000, 28999}, "RJ");
        FAIXAS_UF.put(new int[]{29000, 29999}, "ES");
        FAIXAS_UF.put(new int[]{30000, 39999}, "MG");
        FAIXAS_UF.put(new int[]{40000, 48999}, "BA");
        FAIXAS_UF.put(new int[]{49000, 49999}, "SE");
        FAIXAS_UF.put(new int[]{50000, 56999}, "PE");
        FAIXAS_UF.put(new int[]{57000, 57999}, "AL");
        FAIXAS_UF.put(new int[]{58000, 58999}, "PB");
        FAIXAS_UF.put(new int[]{59000, 59999}, "RN");
        FAIXAS_UF.put(new int[]{60000, 63999}, "CE");
        FAIXAS_UF.put(new int[]{64000, 64999}, "PI");
        FAIXAS_UF.put(new int[]{65000, 65999}, "MA");
        FAIXAS_UF.put(new int[]{66000, 68899}, "PA");
        FAIXAS_UF.put(new int[]{68900, 68999}, "AP");
        FAIXAS_UF.put(new int[]{69000, 69299}, "AM");
        FAIXAS_UF.put(new int[]{69300, 69399}, "RR");
        FAIXAS_UF.put(new int[]{69400, 69899}, "AM");
        FAIXAS_UF.put(new int[]{69900, 69999}, "AC");
        FAIXAS_UF.put(new int[]{70000, 72799}, "DF");
        FAIXAS_UF.put(new int[]{72800, 72999}, "GO");
        FAIXAS_UF.put(new int[]{73000, 73699}, "DF");
        FAIXAS_UF.put(new int[]{73700, 76799}, "GO");
        FAIXAS_UF.put(new int[]{76800, 76999}, "RO");
        FAIXAS_UF.put(new int[]{77000, 77999}, "TO");
        FAIXAS_UF.put(new int[]{78000, 78899}, "MT");
        FAIXAS_UF.put(new int[]{79000, 79999}, "MS");
        FAIXAS_UF.put(new int[]{80000, 87999}, "PR");
        FAIXAS_UF.put(new int[]{88000, 89999}, "SC");
        FAIXAS_UF.put(new int[]{90000, 99999}, "RS");

        registrar("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP", "11");
        registrar("20040-020", "Avenida Rio Branco", "Centro", "Rio de Janeiro", "RJ", "21");
        registrar("30140-071", "Avenida Afonso Pena", "Centro", "Belo Horizonte", "MG", "31");
        registrar("40020-000", "Praça da Sé", "Centro", "Salvador", "BA", "71");
        registrar("50030-230", "Avenida Rio Branco", "Recife", "Recife", "PE", "81");
        registrar("60060-000", "Avenida Duque de Caxias", "Centro", "Fortaleza", "CE", "85");
        registrar("70040-010", "Esplanada dos Ministérios", "Zona Cívico-Administrativa", "Brasília", "DF", "61");
        registrar("80010-010", "Praça Tiradentes", "Centro", "Curitiba", "PR", "41");
        registrar("88010-400", "Praça XV de Novembro", "Centro", "Florianópolis", "SC", "48");
        registrar("90010-150", "Rua dos Andradas", "Centro Histórico", "Porto Alegre", "RS", "51");
    }

    private static void registrar(String cep, String logradouro, String bairro, String cidade, String uf, String ddd) {
        CONHECIDOS.put(cep, Endereco.builder()
                .cep(cep)
                .logradouro(logradouro)
                .bairro(bairro)
                .localidade(cidade)
                .uf(uf)
                .ddd(ddd)
                .fonte(FONTE)
                .build());
    }

    @Override
    public Endereco buscar(String cep) {
        String formatado = Ceps.formatar(cep);
        Endereco conhecido = CONHECIDOS.get(formatado);
        if (conhecido != null) {
            return copiar(conhecido);
        }
        return Endereco.builder()
                .cep(formatado)
                .localidade("Não identificado (modo offline)")
                .uf(ufPorFaixa(formatado))
                .fonte(FONTE)
                .build();
    }

    @Override
    public String identificacao() {
        return "CepOfflineService (tabela local, sem internet)";
    }

    private Endereco copiar(Endereco origem) {
        return Endereco.builder()
                .cep(origem.getCep())
                .logradouro(origem.getLogradouro())
                .complemento(origem.getComplemento())
                .bairro(origem.getBairro())
                .localidade(origem.getLocalidade())
                .uf(origem.getUf())
                .ibge(origem.getIbge())
                .ddd(origem.getDdd())
                .fonte(origem.getFonte())
                .build();
    }

    private String ufPorFaixa(String cep) {
        int prefixo = Integer.parseInt(Ceps.somenteDigitos(cep).substring(0, 5));
        return FAIXAS_UF.entrySet().stream()
                .filter(e -> prefixo >= e.getKey()[0] && prefixo <= e.getKey()[1])
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
