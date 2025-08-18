package app.controller;

import org.springframework.web.bind.annotation.RestController;

import app.model.auth.ApiResponse;
import app.service.LikesService;
import io.jsonwebtoken.Claims;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired
    private LikesService svc;

    @PostMapping("/{postsId}")
    public ResponseEntity<ApiResponse> postsLikes(@PathVariable String postsId,
            @RequestAttribute("claims") Claims token) {
        Integer id = token.get("id", Integer.class);
        Map<String, Object> data = svc.addLikes(postsId, id);

        ApiResponse resp = ApiResponse.builder().status("CREATED").message("berhasil menambahkan likes").data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping("/{postsId}")
    public ResponseEntity<ApiResponse> deleteLikes(
            @PathVariable String postsId,
            @RequestAttribute("claims") Claims token) {
        Integer id = token.get("id", Integer.class);
        Map<String, Object> data = svc.removeLikes(postsId, id);

        ApiResponse resp = ApiResponse.builder().status("DELETED").message("berhasil menhapus likes").data(data)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
