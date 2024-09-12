package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.QuestionDTO;
import ureca.team5.handicine.entity.Question;
import ureca.team5.handicine.repository.QuestionRepository;
import ureca.team5.handicine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<QuestionDTO> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public QuestionDTO getQuestionById(Long id) {
        return questionRepository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Question not found."));
    }

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setUser(userRepository.findByUsername(questionDTO.getAuthorUsername()).orElseThrow(() -> new RuntimeException("User not found.")));
        questionRepository.save(question);
        return convertToDTO(question);
    }

    public QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found."));

        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());

        Question updatedQuestion = questionRepository.save(question);

        return convertToDTO(updatedQuestion);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    private QuestionDTO convertToDTO(Question question) {
        return new QuestionDTO(question.getQuestionId(), question.getTitle(), question.getContent(), question.getUser().getUsername(),
                question.getCreatedAt(), question.getUpdatedAt());
    }
}
