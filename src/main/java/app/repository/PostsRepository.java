package app.repository;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.common.BasePostDTO;
import app.model.entity.DetailedPost;

@Repository
public class PostsRepository {
        @Autowired
        private NamedParameterJdbcTemplate template;

        public Map<String, Object> findById(String id) {

                var sql = """
                                SELECT
                                u.username AS author_username,
                                u.full_name AS author_display_name,
                                u.profile_picture AS author_profile_photo,
                                p.id AS posts_id,
                                p.created_at AS post_created_at,
                                p.parent_id AS post_reply_to,
                                p.text_content AS post_text,
                                p.media_url AS post_media,
                                COUNT(r.id) AS reply_count,
                                CASE WHEN p.parent_id IS NULL THEN true ELSE false END AS parent
                                FROM posts p
                                JOIN users u ON p.author_id = u.id
                                LEFT JOIN posts r ON r.parent_id = p.id
                                WHERE p.parent_id = :parentId
                                OR p.id = :postId
                                GROUP BY u.id,
                                        u.username,
                                        u.full_name,
                                        u.profile_picture,
                                        p.id,
                                        p.created_at,
                                        p.parent_id,
                                        p.text_content,
                                        p.media_url,
                                        parent
                                ORDER BY p.created_at ASC
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("parentId", id, Types.OTHER)
                                .addValue("postId", id, Types.OTHER);
                List<DetailedPost> result = template.query(sql,
                                params,
                                new BeanPropertyRowMapper<DetailedPost>(DetailedPost.class));

                Optional<DetailedPost> parentData = result.stream().filter(data -> {
                        return data.isParent() || data.getPostsId().equals(UUID.fromString(id));
                }).findFirst();

                DetailedPost parent = null;

                if (parentData.isPresent()) {
                        parent = parentData.get();
                        result.remove(parent);
                }

                if (result.size() == 0) {
                        result = null;
                }

                Map<String, Object> returnData = new LinkedHashMap<>();
                returnData.put("parent", parent);
                returnData.put("reply", result);

                return returnData;
        }

        // author id, prent id, text content, media url
        public Map<String, Object> savePostsWithMedia(Integer id, String parentId, String textContent,
                        String mediaFileName) {

                var sql = """
                                insert into posts(author_id, parent_id, text_content, media_url )
                                values(:authorId , :parentId , :textContent , :mediaUrl )
                                returning created_at, id
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("authorId", id, Types.INTEGER)
                                .addValue("parentId", parentId, Types.OTHER)
                                .addValue("textContent", textContent, Types.VARCHAR)
                                .addValue("mediaUrl", mediaFileName, Types.VARCHAR);

                Map<String, Object> resultData = template.queryForMap(sql, params);

                return resultData;
        }

        public List<BasePostDTO> randTimeline() {
                var sql = """
                                                select
                                                u.username as author_username,
                                                u.full_name as author_display_name,
                                                u.profile_picture as author_profile_photo,
                                                p.id as posts_id,
                                                p.created_at as post_created_at,
                                                p.parent_id as post_reply_to,
                                                p.text_content as post_text,
                                                p.media_url as post_media,
                                                count(r.id) as reply_count
                                                from posts p
                                                join users u on p.author_id = u.id
                                                left join posts r on r.parent_id = p.id
                                                where p.parent_id is null
                                                group by u.id,
                                                u.username,
                                                u.full_name,
                                                u.profile_picture,
                                                p.id,
                                                p.created_at,
                                                p.parent_id,
                                                p.text_content,
                                                p.media_url
                                                order by random()
                                                limit 10
                                """;
                List<BasePostDTO> result = template.query(sql,
                                new BeanPropertyRowMapper<>(BasePostDTO.class));
                return result;
        }
}
