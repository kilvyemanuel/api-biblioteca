package com.projetos.biblioteca.exception;

public class ConflitoException extends RuntimeException{
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
