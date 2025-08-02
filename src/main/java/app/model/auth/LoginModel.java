package app.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginModel {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "nama minimal 3 karakter dan maksimal 50")
    private final String username;

    @NotBlank
    @Size(min = 4, max = 50, message = "password minimal 4 karakter dan maksimal 50")
    private final String password;

}
