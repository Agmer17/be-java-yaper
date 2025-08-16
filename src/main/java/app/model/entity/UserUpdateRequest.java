package app.model.entity;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateRequest {
    private String fullName;
    private String bio;
    private String profilePicture;
    private LocalDate birthDay;
    private Boolean isPrivate;
    // yang boleh
    // diupdate itu full_name,bio,pp,birthday,is_private
}