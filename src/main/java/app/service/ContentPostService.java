package app.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.model.entity.DetailedPost;
import app.model.exception.InvalidFileType;
import app.repository.PostsRepository;
import app.utils.FileUtils;
import app.utils.ImageFileVerificator;
import app.utils.ImageFileVerificator.ImageType;

@Service
public class ContentPostService {
    @Autowired
    private PostsRepository repo;

    public List<DetailedPost> getPostDetail(String postId) {
        List<DetailedPost> data = repo.findById(postId);
        return data;
    }

    public Map<String, Object> uploadsNewPosts(String textContent, MultipartFile mediaFile) throws IOException {
        InputStream fileStream = mediaFile.getInputStream();
        ImageType mediaType = ImageFileVerificator.verifyImageType(fileStream);

        if (mediaType == ImageType.UNKNOWN) {
            throw new InvalidFileType("File gambar tidak valid! harap upload png, webp, jpg dan gif");
        }
        String saveFileName = FileUtils.handleUploads(mediaFile, mediaType);

        return Map.of("fileName", saveFileName);
    }
}
