package app.repository;

import java.sql.Types;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import app.model.auth.SignInModel;

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
}
