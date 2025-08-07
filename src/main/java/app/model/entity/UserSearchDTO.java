package app.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchDTO {
    private String displayName;
    private String username;
    private String profilePicture;
    private String bio;
}
