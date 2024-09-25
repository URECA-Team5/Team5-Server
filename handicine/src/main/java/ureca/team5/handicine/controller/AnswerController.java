package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.AnswerDTO;
import ureca.team5.handicine.service.AnswerService;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@CrossOrigin("*")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // 특정 질문글의 모든 답변 조회
    @GetMapping("/{question_id}/answers")
    public ResponseEntity<List<AnswerDTO>> getAllAnswersForQuestion(@PathVariable Long question_id) {
        List<AnswerDTO> answers = answerService.getAnswersByQuestionId(question_id);
        return ResponseEntity.ok(answers);
    }

    // 답변 작성
    @PostMapping("/{question_id}/answers")
    public ResponseEntity<AnswerDTO> createAnswer(@PathVariable Long question_id, @RequestBody AnswerDTO answerDTO) {
        AnswerDTO createdAnswer = answerService.createAnswer(question_id, answerDTO);
        return ResponseEntity.ok(createdAnswer);
    }

    // 답변 수정
    @PatchMapping("/answers/{answer_id}")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable Long answer_id, @RequestBody AnswerDTO answerDTO) {
        AnswerDTO updatedAnswer = answerService.updateAnswer(answer_id, answerDTO);
        return ResponseEntity.ok(updatedAnswer);
    }

    // 답변 삭제
    @DeleteMapping("/answers/{answer_id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answer_id) {
        answerService.deleteAnswer(answer_id);
        return ResponseEntity.noContent().build();
    }
}
