package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;
import io.jsonwebtoken.Claims;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @GetMapping("/@me")
    public ResponseEntity<ApiResponse> getMyAccount(@RequestAttribute("claims") Claims token) {
        int id = token.get("id", Integer.class);
        String username = token.get("username", String.class);

        ResponseEntity<ApiResponse> responseData = ResponseEntity.ok().body(
                ApiResponse.builder()
                        .message("berhasil mengambil data")
                        .data(Map.of("id", id, "username", username)).build());
        return responseData;
    }

}
