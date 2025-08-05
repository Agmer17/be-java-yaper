package app.model.entity;

import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostWithAuthorDTO {
    // author data
    @JsonProperty("author_username")
    private String authorUsername;

    @JsonProperty("author_display_name")
    private String authorDisplayName;

    @JsonProperty("author_profile_photo")
    private String authorProfilePhoto;

    // post metadata
    @JsonProperty("posts_id")
    private UUID postsId;

    @JsonProperty("posts_created_at")
    private Timestamp postCreatedAt;

    @JsonProperty("post_reply_to")
    private UUID postReplyTo;

    @JsonProperty("post_text_content")
    private String postText;

    @JsonProperty("post_media_url")
    private String postMedia;

    @JsonProperty("post_reply_count")
    private int replyCount;

}

// select
// u.username as author_username,
// u.full_name as author_display_name,
// u.profile_picture as author_profile_photo,
// p.id as posts_id,
// p.created_at as post_created_date,
// p.parent_id as post_reply_to,
// p.text_content as posts_text,
// p.media_url as posts_media,

// count(r.id) as reply_count
// from posts p
// join users u on p.author_id = u.id
// left join posts r on r.parent_id = p.id
// where p.parent_id is null and u.username = 'agmer_dev'
// group by u.id,
// u.username,
// u.full_name,
// u.profile_picture,
// p.id,
// p.created_at,
// p.parent_id,
// p.text_content,
// p.media_url
// order by p.created_at desc;
