package app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.EmptyResultDataAccessException;

import app.model.auth.ApiResponse;
import app.model.exception.InvalidFileType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;

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
                .data(e.getMessage()).build();

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == UUID.class) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.builder().status("BAD REQUEST")
                            .message("format id untuk postingan yang kamu masukkan tuh gak valid!").data(null)
                            .build());
        }
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.builder().status("BAD REQUEST")
                        .message("Parameter yang kamu masukkan di url tidak valid! harap periksa kembali!").data(null)
                        .build());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse> handleSaveFileError(IOException ex) {
        return ResponseEntity.badRequest().body(
                ApiResponse.builder().status("ERROR")
                        .message("error saat mengupload file ke server. harap cek kembali file nya").data(ex.getCause())
                        .build());
    }

    @ExceptionHandler(InvalidFileType.class)
    public ResponseEntity<ApiResponse> invalidFileTypeException(InvalidFileType e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.builder().status("ERROR")
                        .message("terjadi error saat memproses gambar")
                        .data(e.getMessage()).build());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> expiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder().status("UNAUTHORIZED").message("Sesi sudah habis silahkan login ulang")
                        .data(e.getMessage()).build());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> JwtExceptionHandler(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder().status("Parsing token error").message("gagal saat parsing token")
                        .data(e.getMessage()).build());
    }
}
