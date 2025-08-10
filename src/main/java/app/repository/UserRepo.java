package app.repository;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.auth.SignInModel;
import app.model.common.BasePostDTO;
import app.model.entity.UserModelResponse;
import app.model.entity.UserSearchDTO;

@Repository
public class UserRepo {
        private NamedParameterJdbcTemplate template;

        public UserRepo(NamedParameterJdbcTemplate template) {
                this.template = template;
        }

        public Map<String, Object> save(SignInModel model, String passwordHash)
                        throws EmptyResultDataAccessException, DataAccessException {
                var sql = """
                                insert into users(username, email, full_name, password_hash)
                                values(:username, :email, :fullName, :passwordHash)
                                returning username, created_at
                                """;

                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("username", model.getUsername(), Types.VARCHAR)
                                .addValue("email", model.getEmail(), Types.VARCHAR)
                                .addValue("fullName", model.getFullName(), Types.VARCHAR)
                                .addValue("passwordHash", passwordHash, Types.VARCHAR);

                Map<String, Object> result = template.queryForMap(sql, params);
                return result;
        }

        public Map<String, Object> getLoginData(String username) throws EmptyResultDataAccessException {
                var sql = """
                                select id, username, password_hash
                                from users
                                where username = :username
                                """;
                MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username,
                                Types.VARCHAR);

                Map<String, Object> result = template.queryForMap(sql, params);

                return result;
        }

        // ini buat get post suatu user
        public List<BasePostDTO> getPostAuthorDataProfile(String username, int id) {
                // var sqlQuery = """
                // select *
                // from posts_with_meta
                // where author_username = :username
                // and post_reply_to is null
                // order by post_created_at desc
                // """;

                var sqlQuery = """
                                select pwm.*,
                                        CASE WHEN ul.user_id IS NOT NULL THEN true ELSE false END AS liked
                                        from posts_with_meta pwm
                                        left join likes ul on ul.posts_id = pwm.posts_id and ul.user_id = :id
                                        where pwm.author_username = :username
                                """;

                MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username)
                                .addValue("id", id, Types.INTEGER);
                List<BasePostDTO> result = template.query(
                                sqlQuery,
                                params,
                                new BeanPropertyRowMapper<>(BasePostDTO.class));
                return result;
        }

        public Map<String, Object> getPublicDataByUsername(String username, int id) {
                var sql = """
                                select
                                    u.username,
                                    u.full_name
                                    as display_name,
                                    u.bio,
                                    u.profile_picture,
                                    u.birthday,
                                    u.created_at as joined_at,
                                    u.is_private as is_private_account
                                    from users u
                                where username = :username
                                """;

                MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username,
                                Types.VARCHAR);

                UserModelResponse result = template.queryForObject(sql, params,
                                new BeanPropertyRowMapper<UserModelResponse>(UserModelResponse.class));
                List<BasePostDTO> postDataResult = this.getPostAuthorDataProfile(username, id);

                LinkedHashMap<String, Object> fullResultData = new LinkedHashMap<>();

                fullResultData.put("current_user_data", result);
                fullResultData.put("post_data", postDataResult);
                return fullResultData;
        }

        public List<UserSearchDTO> findAll(String keyword) {
                var sql = """
                                select username,
                                full_name  as display_name,
                                profile_picture,
                                bio
                                from users
                                where username ilike :query
                                or full_name ilike :query;
                                """;

                MapSqlParameterSource params = new MapSqlParameterSource()
                                .addValue("query", "%" + keyword + "%");

                RowMapper<UserSearchDTO> rowMapper = new BeanPropertyRowMapper<>(UserSearchDTO.class);

                List<UserSearchDTO> result = template.query(sql, params, rowMapper);
                return result;
        }
}
