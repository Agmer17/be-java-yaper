package app.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.repository.LikesRepository;

@Service
public class LikesService {

    @Autowired
    private LikesRepository repo;

    public Map<String, Object> addLikes(String postsId, int userId) {
        Map<String, Object> result = repo.save(postsId, userId);

        return result;
    }

    public Map<String, Object> removeLikes(String postsId, int userId) {
        Map<String, Object> result = repo.remove(postsId, userId);

        return result;
    }
}
