package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.model.auth.ApiResponse;
import app.model.entity.DetailedPost;
import app.model.exception.InvalidFileType;
import app.service.ContentPostService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping(value = "/api/posts")
public class PostsController {
    @Autowired
    private ContentPostService svc;

    @GetMapping("/{postID}")
    public ResponseEntity<ApiResponse> getMethodName(@PathVariable UUID postID) {
        List<DetailedPost> data = svc.getPostDetail(postID.toString());
        ApiResponse response = ApiResponse.builder()
                .status("OK").message("berhasil mengambil data postingan")
                .data(data).build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadNewPosts(
            @RequestParam("text_content") String text,
            @RequestPart("media") MultipartFile media) throws IOException, InvalidFileType {

        Map<String, Object> metaData = svc.uploadsNewPosts(text, media);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder().status("CREATED").message("BERHASIL MENGUPLOAD POSTINGAN!")
                        .data(metaData)
                        .build());
    }

}
