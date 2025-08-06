package app.repository;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.auth.SignInModel;
import app.model.entity.PostWithAuthorDTO;
import app.model.entity.UserModelResponse;

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
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username, Types.VARCHAR);

        Map<String, Object> result = template.queryForMap(sql, params);

        return result;
    }

    // ini buat get post suatu user
    public List<PostWithAuthorDTO> getPostAuthorDataProfile(String username) {
        var sqlQuery = """
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
                where p.parent_id is null and u.username = :username
                group by u.id,
                    u.username,
                    u.full_name,
                    u.profile_picture,
                    p.id,
                    p.created_at,
                    p.parent_id,
                    p.text_content,
                    p.media_url
                order by p.created_at desc
                        """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username);
        List<PostWithAuthorDTO> result = template.query(
                sqlQuery,
                params,
                new BeanPropertyRowMapper<>(PostWithAuthorDTO.class));
        return result;
    }

    public Map<String, Object> getPublicDataByUsername(String username) {
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

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("username", username, Types.VARCHAR);

        UserModelResponse result = template.queryForObject(sql, params,
                new BeanPropertyRowMapper<UserModelResponse>(UserModelResponse.class));
        List<PostWithAuthorDTO> postDataResult = this.getPostAuthorDataProfile(username);

        LinkedHashMap<String, Object> fullResultData = new LinkedHashMap<>();

        fullResultData.put("current_user_data", result);
        fullResultData.put("post_data", postDataResult);
        return fullResultData;
    }
}
