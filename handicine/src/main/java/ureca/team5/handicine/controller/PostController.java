package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.PostDTO;
import ureca.team5.handicine.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@CrossOrigin("*")
public class PostController {

    @Autowired
    private PostService postService;

    // 자유게시글 전체 조회
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/{post_id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long post_id) {
        PostDTO post = postService.getPostById(post_id);
        return ResponseEntity.ok(post);
    }

    // 새 게시글 작성
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(postDTO);
        return ResponseEntity.ok(createdPost);
    }

    // 게시글 수정
    @PatchMapping("/{post_id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long post_id, @RequestBody PostDTO postDTO) {
        PostDTO updatedPost = postService.updatePost(post_id, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/{post_id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long post_id) {
        postService.deletePost(post_id);
        return ResponseEntity.noContent().build();
    }
}