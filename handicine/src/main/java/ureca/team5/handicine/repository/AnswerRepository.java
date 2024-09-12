package ureca.team5.handicine.repository;

import ureca.team5.handicine.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);  // 특정 질문에 대한 답변 목록
}