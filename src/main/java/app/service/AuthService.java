package app.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import app.model.auth.LoginModel;
import app.model.auth.SignInModel;
import app.model.exception.ResourceNotFoundExeption;
import app.repository.UserRepo;
import app.utils.JWTUtil;
import app.utils.PasswordUtils;

@Service
public class AuthService {
    private final UserRepo repo;
    private final JWTUtil jwt;

    public AuthService(UserRepo repo, JWTUtil jwt) {
        this.repo = repo;
        this.jwt = jwt;
    }

    public Map<String, Object> createNewUser(SignInModel newUser) {
        String hashedPassword = PasswordUtils.HashPw(newUser.getPassword());
        Map<String, Object> data = repo.save(newUser, hashedPassword);

        return data;
    }

    /*
     * method buat login. nanti kalo berhasil returnya
     * { ....
     * data : {
     * accessToken: "blablabla"
     * }
     * }
     */
    public Map<String, Object> loginService(LoginModel loginData) {
        String plainPw = loginData.getPassword();

        Map<String, Object> userData = null;
        try {
            userData = repo.getLoginData(loginData.getUsername());

        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundExeption("Akun tidak ditemukan!", HttpStatus.NOT_FOUND);
        }

        boolean verifyPassword = BCrypt.checkpw(plainPw, userData.get("password_hash").toString());

        if (verifyPassword) {
            Integer id = Integer.valueOf(userData.get("id").toString());
            Map<String, Object> jwtData = new LinkedHashMap<>();
            String accessToken = jwt.generateToken(id, loginData.getUsername());

            jwtData.put("accessToken", accessToken);

            return jwtData;
        }

        return null;

    }
}
