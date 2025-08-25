package app.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.common.BasePostDTO;
import app.model.entity.DetailedPost;
import app.model.exception.ResourceNotFoundExeption;

@Repository
public class PostsRepository {
        @Autowired
        private NamedParameterJdbcTemplate template;

        public Map<String, List<DetailedPost>> findById(String id, int userId) {
                // CAST(:input_id AS uuid) AS input_id,

                var sql = """
                                WITH input_data AS (
                                            SELECT
                                                CAST( :input_id AS uuid) AS input_id,
                                                parent_id AS parent_of_input
                                            FROM posts
                                            WHERE id = CAST( :input_id AS uuid)
                                        )
                                        SELECT pwm.*,
                                            CASE
                                                WHEN pwm.posts_id = idata.parent_of_input THEN 'root'
                                                WHEN pwm.posts_id = idata.input_id THEN 'current'
                                                ELSE 'reply'
                                            END AS status,
                                            CASE
                                        	 WHEN ul.user_id IS NOT NULL THEN true ELSE false
                                             END AS liked
                                        FROM posts_with_meta pwm
                                        JOIN input_data idata ON true
                                        left join likes ul on ul.posts_id = pwm.posts_id and ul.user_id = :userId
                                        WHERE
                                            post_reply_to = idata.input_id
                                            OR pwm.posts_id = idata.input_id
                                            OR pwm.posts_id = idata.parent_of_input
                                        ORDER BY pwm.post_created_at ASC
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("input_id", id, Types.OTHER)
                                .addValue("userId", userId, Types.INTEGER);

                List<DetailedPost> result = null;
                try {
                        result = template.query(sql,
                                        params,
                                        new BeanPropertyRowMapper<DetailedPost>(DetailedPost.class));

                } catch (ResourceNotFoundExeption e) {
                        throw new ResourceNotFoundExeption("Postingan tidak ditemukan!", HttpStatus.NOT_FOUND);
                }

                // System.out.println(result +
                // "\n\n\n\n\n\n\\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

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

        public List<BasePostDTO> randTimeline(int id) {
                var sql = """
                                select pwm.*,
                                CASE WHEN ul.user_id IS NOT NULL THEN true ELSE false END AS liked
                                from posts_with_meta pwm
                                left join likes ul on ul.posts_id = pwm.posts_id and ul.user_id = :id
                                order by random()
                                limit 10;
                                """;

                MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id, Types.INTEGER);
                List<BasePostDTO> result = template.query(sql, params,
                                new BeanPropertyRowMapper<>(BasePostDTO.class));
                return result;
        }

        public List<BasePostDTO> findAll(String query, int id) {
                var sql = """
                                select pwm.*,
                                        CASE WHEN ul.user_id IS NOT NULL THEN true ELSE false END AS liked
                                        from posts_with_meta pwm
                                        left join likes ul on ul.posts_id = pwm.posts_id and ul.user_id = :id
                                        where to_tsvector('indonesian',coalesce(pwm.post_text ) || ' ' || coalesce(pwm.author_display_name)) @@ to_tsquery( :query )
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id, Types.INTEGER)
                                .addValue("query", query, Types.VARCHAR);

                List<BasePostDTO> rs = template.query(sql, params, new BeanPropertyRowMapper<>(BasePostDTO.class));

                return rs;
        }
}