package ureca.team5.handicine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ureca.team5.handicine.dto.CommentDTO;
import ureca.team5.handicine.entity.Comment;
import ureca.team5.handicine.entity.Post;
import ureca.team5.handicine.entity.User;
import ureca.team5.handicine.repository.CommentRepository;
import ureca.team5.handicine.repository.PostRepository;
import ureca.team5.handicine.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // 특정 게시글의 모든 댓글 조회
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostPostId(postId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 댓글 생성
    public CommentDTO createComment(Long postId, CommentDTO commentDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);

        // 댓글 작성자 설정
        User user = userRepository.findByUsername(commentDTO.getAuthorUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        comment.setUser(user);

        commentRepository.save(comment);
        return convertToDTO(comment);
    }

    // 댓글 수정
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);

        return convertToDTO(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    // Comment -> CommentDTO 변환 메서드
    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getCommentId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getPost().getPostId(),
                comment.getCreatedAt()
        );
    }
}
