package app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.model.entity.UserSearchDTO;
import app.model.entity.UserUpdateRequest;
import app.repository.UserRepo;
import app.utils.FileUtils;
import app.utils.ImageFileVerificator;
import app.utils.ImageFileVerificator.ImageType;

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

    public Map<String, Object> updateUserData(
            String fullName,
            String bio,
            MultipartFile img,
            LocalDate birthday,
            Boolean isPrivate,
            int id) throws IOException {

        String fileName = null;
        if (img != null) {
            ImageType imgType = ImageFileVerificator.verifyImageType(img.getInputStream());
            fileName = FileUtils.handleUploads(img, imgType);
        }

        UserUpdateRequest payload = new UserUpdateRequest(fullName, bio, fileName, birthday, isPrivate);
        Map<String, Object> result = repo.updateUser(payload, id);

        return result;
    }
}
