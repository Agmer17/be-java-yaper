package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.model.auth.ApiResponse;
import app.model.entity.DetailedPost;
import app.model.entity.PostWithAuthorDTO;
import app.model.exception.InvalidFileType;
import app.service.ContentPostService;
import io.jsonwebtoken.Claims;

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
import org.springframework.web.bind.annotation.RequestAttribute;

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
                        @RequestParam(value = "text_content", required = false) String text,
                        @RequestPart(value = "media", required = false) MultipartFile media,
                        @RequestParam(value = "parent_id", required = false) String parentId,
                        @RequestAttribute("claims") Claims token) throws IOException, InvalidFileType {

                if ((text == null || text.trim().isEmpty()) && (media == null || media.isEmpty())) {
                        return ResponseEntity.badRequest().body(
                                        ApiResponse.builder().status("ERROR")
                                                        .message("Kamu harus mengisi salah satu field!").data(null)
                                                        .build());
                }
                Integer id = token.get("id", Integer.class);

                Map<String, Object> metaData = svc.uploadsNewPosts(text, media, id, parentId);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponse.builder().status("CREATED").message("BERHASIL MENGUPLOAD POSTINGAN!")
                                                .data(metaData)
                                                .build());
        }

        @GetMapping("/timeline")
        public ResponseEntity<ApiResponse> getMethodName() {
                List<PostWithAuthorDTO> timelineData = svc.getTimelinePosts();

                ApiResponse responseBody = ApiResponse.builder().status("Success")
                                .message("berhasil mengambil data timeline").data(timelineData).build();

                return ResponseEntity.ok().body(responseBody);
        }

}
