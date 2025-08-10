package app.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        public Map<String, List<DetailedPost>> findById(String id) {

                var sql = """
                                WITH input_data AS (
                                            SELECT
                                                CAST(:input_id AS uuid) AS input_id,
                                                parent_id AS parent_of_input
                                            FROM posts
                                            WHERE id = CAST(:input_id AS uuid)
                                        )
                                        SELECT *,
                                            CASE
                                                WHEN posts_id = idata.parent_of_input THEN 'root'
                                                WHEN posts_id = idata.input_id THEN 'current'
                                                ELSE 'reply'
                                            END AS status
                                        FROM posts_with_meta
                                        JOIN input_data idata ON TRUE
                                        WHERE
                                            post_reply_to = idata.input_id
                                            OR posts_id = idata.input_id
                                            OR posts_id = idata.parent_of_input
                                        ORDER BY post_created_at ASC
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("input_id", id, Types.OTHER);
                List<DetailedPost> result = template.query(sql,
                                params,
                                new BeanPropertyRowMapper<DetailedPost>(DetailedPost.class));

                Map<String, List<DetailedPost>> returnData = new LinkedHashMap<>();
                returnData.put("root", new ArrayList<>());
                returnData.put("current", new ArrayList<>());
                returnData.put("reply", new ArrayList<>());

                for (DetailedPost post : result) {
                        // Ambil status dari objek DTO, misal ada field postsStatus
                        String status = post.getStatus();

                        // Masukin ke group sesuai status
                        returnData.computeIfAbsent(status, _ -> new ArrayList<>())
                                        .add(post);
                }

                returnData.entrySet()
                                .removeIf(entry -> (entry.getKey().equals("root") || entry.getKey().equals("reply"))
                                                && entry.getValue().isEmpty());

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
                                select *
                                from posts_with_meta
                                where post_reply_to is null
                                order by random()
                                limit 10
                                """;
                List<BasePostDTO> result = template.query(sql,
                                new BeanPropertyRowMapper<>(BasePostDTO.class));
                return result;
        }
}
