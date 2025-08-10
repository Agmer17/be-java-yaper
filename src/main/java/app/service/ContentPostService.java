package app.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import app.model.common.BasePostDTO;
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

    public Map<String, List<DetailedPost>> getPostDetail(String postId) {
        Map<String, List<DetailedPost>> data = repo.findById(postId);
        return data;
    }

    public Map<String, Object> uploadsNewPosts(String textContent, MultipartFile mediaFile, Integer id, String parentId)
            throws IOException {

        if (mediaFile != null) {
            InputStream fileStream = mediaFile.getInputStream();
            ImageType mediaType = ImageFileVerificator.verifyImageType(fileStream);

            if (mediaType == ImageType.UNKNOWN) {
                throw new InvalidFileType("File gambar tidak valid! harap upload png, webp, jpg dan gif");
            }

            // media file name if exists
            String saveFileName = FileUtils.handleUploads(mediaFile, mediaType);

            Map<String, Object> saveResult = repo.savePostsWithMedia(id, parentId, textContent, saveFileName);

            return saveResult;
        } else {
            Map<String, Object> saveResult = repo.savePostsWithMedia(id, parentId, textContent, null);
            return saveResult;
        }

    }

    public List<BasePostDTO> getTimelinePosts() {
        List<BasePostDTO> timelinePosts = repo.randTimeline();

        return timelinePosts;
    }
}
