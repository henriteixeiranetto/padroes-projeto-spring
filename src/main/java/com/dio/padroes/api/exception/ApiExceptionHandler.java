package com.dio.padroes.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

// respostas de erro no formato ProblemDetail (RFC 7807), com uma lista do que precisa ser corrigido
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage(), List.of(e.getMessage()), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ProblemDetail tratarRegraDeNegocio(RegraDeNegocioException e, HttpServletRequest request) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio violada", e.getMessage(), e.getErros(), request);
    }

    @ExceptionHandler(IntegracaoIndisponivelException.class)
    public ProblemDetail tratarIntegracao(IntegracaoIndisponivelException e, HttpServletRequest request) {
        log.warn("Integracao externa indisponivel: {}", e.getMessage());
        return montar(HttpStatus.SERVICE_UNAVAILABLE, "Serviço externo indisponível",
                "Não foi possível consultar o ViaCEP agora. Tente novamente em instantes.",
                List.of(e.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarCamposInvalidos(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> erros = e.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getDefaultMessage() == null
                        ? campo.getField() + ": inválido"
                        : campo.getDefaultMessage())
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Alguns campos precisam ser corrigidos", erros, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail tratarArgumentoInvalido(IllegalArgumentException e, HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida", e.getMessage(), List.of(e.getMessage()), request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception e, HttpServletRequest request) {
        log.error("Erro inesperado em {}", request.getRequestURI(), e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado",
                "Algo deu errado do nosso lado. Consulte os logs da aplicação.",
                List.of(e.getClass().getSimpleName()), request);
    }

    private ProblemDetail montar(HttpStatus status, String titulo, String detalhe,
                                 List<String> erros, HttpServletRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setType(URI.create("https://http.dev/" + status.value()));
        problema.setProperty("erros", erros);
        problema.setProperty("caminho", request.getRequestURI());
        problema.setProperty("momento", LocalDateTime.now());
        return problema;
    }
}
