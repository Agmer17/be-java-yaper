package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @GetMapping("/@me")
    public ResponseEntity<ApiResponse> getMyAccount(@RequestHeader("Authorization") String auth) {
        return null;
    }

}
