package app.model.common;

import org.springframework.http.HttpStatus;

public class CommonExceptionSchema extends RuntimeException {
    private HttpStatus status;

    public CommonExceptionSchema(String message, HttpStatus code) {
        super(message);
        this.status = code;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
