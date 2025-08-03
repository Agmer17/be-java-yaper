package app.model.entity;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserModelResponse {
    private String username;
    private String displayName;
    private String bio;
    private String profilePicture;
    private Date birthday;
    private Timestamp joinedAt;
    private boolean isPrivateAccount;

}
