package app.model.common;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasePostDTO {
    @JsonProperty("author_username")
    private String authorUsername;

    @JsonProperty("author_display_name")
    private String authorDisplayName;

    @JsonProperty("author_profile_photo")
    private String authorProfilePhoto;

    @JsonProperty("posts_id")
    private UUID postsId;

    @JsonProperty("post_reply_to")
    private UUID postReplyTo;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("post_media")
    private String postMedia;

    @JsonProperty("reply_count")
    private int replyCount;

    @JsonProperty("post_created_at")
    private LocalDateTime postCreatedAt;

    @JsonProperty("likes_count")
    private int totalLikes;

    private boolean liked;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BasePostDTO))
            return false;
        BasePostDTO that = (BasePostDTO) o;
        return Objects.equals(postsId, that.postsId);
    }
}
