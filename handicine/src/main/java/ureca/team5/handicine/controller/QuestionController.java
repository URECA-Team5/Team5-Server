package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.QuestionDTO;
import ureca.team5.handicine.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@CrossOrigin("*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 전체 질문글 조회
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<QuestionDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // 질문글 상세조회
    @GetMapping("/{question_id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable("question_id") Long question_id) {
        QuestionDTO question = questionService.getQuestionById(question_id);
        return ResponseEntity.ok(question);
    }

    // 질문글 작성
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
        return ResponseEntity.ok(createdQuestion);
    }

    // 질문글 수정
    @PatchMapping("/{question_id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable("question_id") Long question_id, @RequestBody QuestionDTO questionDTO) {
        QuestionDTO updatedQuestion = questionService.updateQuestion(question_id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    // 질문글 삭제
    @DeleteMapping("/{question_id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("question_id") Long question_id) {
        questionService.deleteQuestion(question_id);
        return ResponseEntity.noContent().build();
    }
}
