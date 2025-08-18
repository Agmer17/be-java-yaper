package app.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.model.entity.UserSearchDTO;
import app.model.entity.UserUpdateRequest;
import app.model.exception.InvalidRequestData;
import app.model.exception.ResourceNotFoundExeption;
import app.repository.UserRepo;
import app.utils.FileUtils;
import app.utils.ImageFileVerificator;
import app.utils.ImageFileVerificator.ImageType;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public Map<String, Object> getUserData(String username, int id) {
        Map<String, Object> model = null;
        try {
            model = repo.getPublicDataByUsername(username, id);

        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundExeption("akun tidak ditemukan!", HttpStatus.NOT_FOUND);
        }

        return model;
    }

    public List<UserSearchDTO> searchAll(String keyword) {

        List<UserSearchDTO> resultList = repo.findAll(keyword);

        return resultList;
    }

    public UserUpdateRequest updateUserData(
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

        if (!isAllFieldsNull(payload)) {
            UserUpdateRequest result = repo.updateUser(payload, id);

            return result;

        } else {
            throw new InvalidRequestData("Data yang dikirim tidak boleh kosong semua!", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isAllFieldsNull(UserUpdateRequest user) {
        if (user == null)
            return true;

        try {
            for (Field field : user.getClass().getDeclaredFields()) {
                field.setAccessible(true); // bisa akses private field
                Object value = field.get(user);
                if (value != null) {
                    return false; // ada field yg tidak null
                }
            }
        } catch (IllegalAccessException e) {
            throw new InvalidRequestData("Gagal saat mengakses data! harap cek kembali data yang dikirim!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return true; // semua null
    }
}
