package app.model.entity;

import app.model.common.BaseUserData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchDTO extends BaseUserData {
    private String bio;
}
