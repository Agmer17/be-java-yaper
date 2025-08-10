package app.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.model.entity.UserSearchDTO;
import app.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public Map<String, Object> getUserData(String username, int id) {
        Map<String, Object> model = repo.getPublicDataByUsername(username, id);

        return model;
    }

    public List<UserSearchDTO> searchAll(String keyword) {
        List<UserSearchDTO> resultList = repo.findAll(keyword);

        return resultList;
    }
}
