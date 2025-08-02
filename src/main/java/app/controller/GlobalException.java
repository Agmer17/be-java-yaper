package app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.EmptyResultDataAccessException;

import app.model.auth.ApiResponse;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ApiResponse resp = ApiResponse.builder().status("BAD_REQUEST").message("Harap isi semua field secara benar")
                .data(errors).build();
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = e.getMessage().toLowerCase();
        String errorDetail = "Data validation failed";

        if (message.contains("unique") && message.contains("username")) {
            errorDetail = "Username Sudah terdaftar";
        } else if (message.contains("unique") && message.contains("email")) {
            errorDetail = "Email Sudah terdaftar";
        } else if (message.contains("not null")) {
            errorDetail = "harap isi semua field";
        }

        ApiResponse errResponse = ApiResponse.builder().status("CONFLICT").message(errorDetail).data(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errResponse); // 409 Conflict
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ApiResponse> handleConnectionFailure(DataAccessResourceFailureException e) {
        e.printStackTrace();

        ApiResponse resp = ApiResponse.builder().status("SERVICE UNAVAIBLE")
                .message("TERJADI KESALAHAN DI SERVER! HARAP COBA LAGI NANTI").data(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(resp);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiResponse> notFoundException(EmptyResultDataAccessException e) {
        ApiResponse resp = ApiResponse.builder().status("NOT FOUND").message("AKUN ATAU POSTINGAN TIDAK DITEMUKAN!")
                .data(null).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ApiResponse> handleBadSqlGrammar(BadSqlGrammarException e) {
        e.printStackTrace();
        ApiResponse resp = ApiResponse.builder().status("INTERNAL SERVER ERROR")
                .message("TERJADI KESALAHAN DI SERVER! HARAP COBA LAGI NANTI!").data(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resp);
    }
}
