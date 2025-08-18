package app.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import app.model.exception.ResourceNotFoundExeption;
import app.repository.LikesRepository;

@Service
public class LikesService {

    @Autowired
    private LikesRepository repo;

    public Map<String, Object> addLikes(String postsId, int userId) {

        Map<String, Object> result = null;
        try {
            result = repo.save(postsId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundExeption("kamu sudah like postingan ini sebelumnya!", HttpStatus.CONFLICT);
        }
        return result;
    }

    public Map<String, Object> removeLikes(String postsId, int userId) {
        Map<String, Object> result = null;
        try {
            result = repo.remove(postsId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundExeption("likes tidak ditemukan!", HttpStatus.GONE);
        }
        return result;
    }
}
