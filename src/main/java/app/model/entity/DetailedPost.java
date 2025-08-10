package app.model.entity;

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

    @JsonProperty("posts_node_status")
    private String status;

}
