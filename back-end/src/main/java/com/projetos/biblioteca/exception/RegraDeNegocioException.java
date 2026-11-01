package com.projetos.biblioteca.exception;

public class RegraDeNegocioException extends RuntimeException{
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
