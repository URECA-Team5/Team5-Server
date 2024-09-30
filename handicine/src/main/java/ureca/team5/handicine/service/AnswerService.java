package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.AnswerDTO;
import ureca.team5.handicine.entity.Answer;
import ureca.team5.handicine.entity.Question;
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
        List<Answer> answers = answerRepository.findByQuestionQuestionId(questionId);
        return answers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AnswerDTO createAnswer(Long questionId, AnswerDTO answerDTO) {
    	System.out.println("createAnswer");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());
        answer.setUser(userRepository.findByUsername(answerDTO.getAuthorUsername())
                .orElseThrow(() -> new RuntimeException("User not found.")));
        answer.setQuestion(question);
        
        Answer savedAnswer = answerRepository.save(answer);
        return convertToDTO(savedAnswer);
    }

    public AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found."));
        answer.setContent(answerDTO.getContent());
        Answer updatedAnswer = answerRepository.save(answer);
        return convertToDTO(updatedAnswer);
    }

    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    private AnswerDTO convertToDTO(Answer answer) {
        return new AnswerDTO(answer.getAnswerId(), answer.getContent(), answer.getUser().getUsername(),
                answer.getCreatedAt(), answer.getUpdatedAt(), answer.getQuestion().getQuestionId());
    }
}