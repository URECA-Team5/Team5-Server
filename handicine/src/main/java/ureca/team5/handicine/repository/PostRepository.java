package ureca.team5.handicine.repository;

import ureca.team5.handicine.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserUserId(Long userId);  // 특정 사용자가 작성한 게시글 목록
}