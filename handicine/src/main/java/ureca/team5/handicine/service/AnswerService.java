package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.AnswerDTO;
import ureca.team5.handicine.entity.Answer;
import ureca.team5.handicine.repository.AnswerRepository;
import ureca.team5.handicine.repository.QuestionRepository;
import ureca.team5.handicine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AnswerDTO> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());
        answer.setQuestion(questionRepository.findById(answerDTO.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found.")));
        answer.setUser(userRepository.findByUsername(answerDTO.getAuthorUsername())
                .orElseThrow(() -> new RuntimeException("User not found.")));
        answerRepository.save(answer);
        return convertToDTO(answer);
    }

    public void updateAnswer(Long id, AnswerDTO answerDTO) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found."));
        answer.setContent(answerDTO.getContent());
        answerRepository.save(answer);
    }

    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    private AnswerDTO convertToDTO(Answer answer) {
        return new AnswerDTO(answer.getAnswerId(), answer.getContent(), answer.getUser().getUsername(),
                answer.getCreatedAt(), answer.getUpdatedAt(), answer.getQuestion().getQuestionId());
    }
}