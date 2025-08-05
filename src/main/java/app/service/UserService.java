package app.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public Map<String, Object> getUserData(String username) {
        Map<String, Object> model = repo.getPublicDataByUsername(username);

        return model;
    }
}
