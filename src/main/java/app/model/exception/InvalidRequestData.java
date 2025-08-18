package app.model.exception;

import org.springframework.http.HttpStatus;

import app.model.common.CommonExceptionSchema;

public class InvalidRequestData extends CommonExceptionSchema {

    public InvalidRequestData(String message, HttpStatus code) {
        super(message, code);
    }

}
