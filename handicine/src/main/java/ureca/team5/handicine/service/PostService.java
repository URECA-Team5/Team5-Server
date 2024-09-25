package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.PostDTO;
import ureca.team5.handicine.entity.Post;
import ureca.team5.handicine.repository.PostRepository;
import ureca.team5.handicine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        return postRepository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Post not found."));
    }

    public PostDTO createPost(PostDTO postDTO) {
    	System.out.println("PostDTO createPost");
    	System.out.println("제목 : "+postDTO.getTitle());
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUser(userRepository.findByUsername(postDTO.getAuthorUsername())
                .orElseThrow(() -> new RuntimeException("User not found.")));
        postRepository.save(post);
        return convertToDTO(post);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found."));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private PostDTO convertToDTO(Post post) {
        return new PostDTO(post.getPostId(), post.getTitle(), post.getContent(),
                post.getUser().getUsername(), post.getCreatedAt(), post.getUpdatedAt());
    }
}