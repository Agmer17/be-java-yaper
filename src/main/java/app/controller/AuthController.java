package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;
import app.model.auth.LoginModel;
import app.model.auth.SignInModel;
import app.service.AuthService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
 * INI ADALAH CLASS CONTROLLER BUAT AUTHENTICATION
 * LOGIN, SIGN IN, RESET PW DLL.
 */

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final AuthService svc;

    public AuthController(AuthService service) {
        this.svc = service;
    }

    /*
     * method buat handle request sign in. Nanti return nya itu
     * {
     * status : created,
     * message : "berhasil membuat akun! silahkan login",
     * data : {
     * username : "username",
     * created_at : "tanggal akun dibuat"
     * }
     * }
     */
    @PostMapping(value = "/sign-in")
    public ResponseEntity<ApiResponse> SignInHandler(@RequestBody @Valid SignInModel newUser) {
        Map<String, Object> resData = svc.createNewUser(newUser);
        ApiResponse response = ApiResponse.builder()
                .status("success")
                .message("berhasil membuat akun")
                .data(resData)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> postMethodName(@RequestBody @Valid LoginModel loginData) {
        Map<String, Object> data = svc.loginService(loginData);

        if (data != null) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            ApiResponse.builder().status("OK")
                                    .message("LOGIN SUKSES")
                                    .data(data).build());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder().status("UNAUTHORIZED")
                        .message("Username atau password salah!")
                        .data(null)
                        .build());
    }

}
