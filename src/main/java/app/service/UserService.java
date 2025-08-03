package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.model.entity.UserModelResponse;
import app.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public UserModelResponse getCurrentUserData(String username) {
        UserModelResponse model = repo.getPublicDataByUsername(username);

        return model;
    }
}
