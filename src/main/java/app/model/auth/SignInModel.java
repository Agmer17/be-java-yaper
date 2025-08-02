package app.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInModel {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "nama minimal 3 karakter dan maksimal 50")
    private final String username;

    @NotBlank
    @Size(min = 4, max = 100, message = "email minimal 4 karakter dan maksimal 50")
    @Email(message = "format email tidak valid")
    private final String email;

    @NotBlank
    @Size(min = 4, max = 50, message = "full_name harus antara 4 dan 50 karakter")
    @JsonProperty("full_name")
    private final String fullName;

    @NotBlank
    @Size(min = 4, max = 50, message = "password minimal 4 karakter dan maksimal 50")
    private final String password;
}
