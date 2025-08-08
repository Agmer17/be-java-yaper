package app.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import app.model.common.BasePostDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DetailedPost extends BasePostDTO {

    @JsonProperty("is_parent")
    private boolean parent;

}
