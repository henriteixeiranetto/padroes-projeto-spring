package com.dio.padroes.support;

public final class Ceps {

    private Ceps() {
    }

    public static String somenteDigitos(String cep) {
        return cep == null ? "" : cep.replaceAll("[^0-9]", "");
    }

    public static boolean valido(String cep) {
        return somenteDigitos(cep).length() == 8;
    }

    public static String formatar(String cep) {
        String digitos = somenteDigitos(cep);
        if (digitos.length() != 8) {
            return cep;
        }
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }

    // primeiro digito do CEP = regiao postal (0 = Grande Sao Paulo ... 9 = RS)
    public static int regiao(String cep) {
        String digitos = somenteDigitos(cep);
        if (digitos.isEmpty()) {
            throw new IllegalArgumentException("CEP invalido: " + cep);
        }
        return Character.getNumericValue(digitos.charAt(0));
    }
}
