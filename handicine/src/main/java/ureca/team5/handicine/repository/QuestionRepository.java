package ureca.team5.handicine.repository;

import ureca.team5.handicine.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByUserUserId(Long userId);  // 특정 사용자가 작성한 질문 목록
}