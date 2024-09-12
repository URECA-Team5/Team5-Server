package ureca.team5.handicine.repository;

import ureca.team5.handicine.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostPostId(Long postId);  // 특정 게시글에 달린 댓글 목록
}