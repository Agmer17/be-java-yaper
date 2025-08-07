package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;
import app.model.entity.UserSearchDTO;
import app.service.UserService;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService svc;

    @GetMapping("/@me")
    public ResponseEntity<ApiResponse> getMyAccount(@RequestAttribute("claims") Claims token) {
        String username = token.get("username", String.class);
        Map<String, Object> userData = svc.getUserData(username);
        ApiResponse response = ApiResponse.builder()
                .status("OK")
                .message("BERHASIL MENGAMBIL DATA")
                .data(userData)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse> getOtherUserProfile(@PathVariable String username) {
        Map<String, Object> otherUserData = svc.getUserData(username);
        ApiResponse response = ApiResponse.builder()
                .status("OK")
                .message("Berhasil mengambil data")
                .data(otherUserData)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> seacrhAllUser(@RequestParam String key) {
        List<UserSearchDTO> data = svc.searchAll(key);

        ApiResponse respBody = ApiResponse.builder().status("success").message("user ditemukan").data(data).build();

        if (data.isEmpty()) {
            throw new EmptyResultDataAccessException("user dengan username " + key + " tidak ditemukan!", 1);
        }

        return ResponseEntity.ok().body(respBody);
    }

}
