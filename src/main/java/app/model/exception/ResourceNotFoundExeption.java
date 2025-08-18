package app.model.exception;

import org.springframework.http.HttpStatus;

import app.model.common.CommonExceptionSchema;

public class ResourceNotFoundExeption extends CommonExceptionSchema {

    public ResourceNotFoundExeption(String message, HttpStatus code) {
        super(message, code);
    }
}
