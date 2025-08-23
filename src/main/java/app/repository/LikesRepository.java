package app.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.common.BasePostDTO;
import app.model.common.BaseUserData;

@Repository
public class LikesRepository {
        @Autowired
        private NamedParameterJdbcTemplate template;

        public Map<String, Object> save(String postsId, int userId) {
                var query = """
                                insert into likes(posts_id, user_id )
                                values ( :postsId, :userId )
                                on conflict do nothing
                                returning posts_id, user_id, created_at
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("postsId", postsId, Types.OTHER)
                                .addValue("userId", userId, Types.INTEGER);
                Map<String, Object> rs = template.queryForMap(query, params);
                return rs;
        }

        public Map<String, Object> remove(String postsId, int userId) {
                var query = """
                                delete from likes
                                where posts_id = :postsId
                                and user_id = :userId
                                returning now() as deleted_date
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("postsId", postsId, Types.OTHER)
                                .addValue("userId", userId, Types.INTEGER);
                Map<String, Object> rs = template.queryForMap(query, params);
                return rs;
        }

        public List<BaseUserData> getFromPosts(String postsId) {
                var query = """
                                select u.full_name as display_name,
                                                u.username,
                                                u.profile_picture
                                                from likes l
                                                left join users u on u.id = l.user_id
                                                where posts_id = :postsId
                                                """;

                MapSqlParameterSource params = new MapSqlParameterSource().addValue("postsId", postsId, Types.OTHER);
                List<BaseUserData> rs = template.query(query, params, new BeanPropertyRowMapper<>(BaseUserData.class));

                return rs;
        }

        public List<BasePostDTO> getFromUser(String username, int id) {
                var sql = """
                                select pwm.*,
                                       case when l_login.user_id is not null then true else false end as liked
                                from likes l_target
                                join users u_target on u_target.id = l_target.user_id
                                join posts_with_meta pwm on pwm.posts_id = l_target.posts_id
                                left join users u_login
                                       on u_login.id = :id
                                left join likes l_login
                                       on l_login.posts_id = pwm.posts_id
                                      and l_login.user_id = u_login.id
                                where u_target.username = :username
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id, Types.INTEGER)
                                .addValue("username", username, Types.VARCHAR);

                List<BasePostDTO> rs = template.query(sql, params, new BeanPropertyRowMapper<>(BasePostDTO.class));

                return rs;
        }
}
