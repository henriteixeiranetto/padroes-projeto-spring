package com.dio.padroes.pattern.template;

import java.time.LocalDateTime;

public record Notificacao(String canal, String destinatario, String assunto, String corpo, LocalDateTime enviadaEm) {
}
