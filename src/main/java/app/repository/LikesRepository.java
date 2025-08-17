package app.repository;

import java.sql.Types;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
