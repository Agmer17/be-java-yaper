package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;
// import app.model.entity.UserModelResponse;
import app.service.UserService;
import io.jsonwebtoken.Claims;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService svc;

    @GetMapping("/@me")
    public ResponseEntity<ApiResponse> getMyAccount(@RequestAttribute("claims") Claims token) {
        String username = token.get("username", String.class);
        Map<String, Object> userData = svc.getCurrentUserData(username);
        ApiResponse response = ApiResponse.builder()
                .status("OK")
                .message("BERHASIL MENGAMBIL DATA")
                .data(userData)
                .build();
        return ResponseEntity.ok().body(response);
    }

}
