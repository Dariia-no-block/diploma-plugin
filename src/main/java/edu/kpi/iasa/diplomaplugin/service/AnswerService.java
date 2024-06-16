package edu.kpi.iasa.diplomaplugin.service;

import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import edu.kpi.iasa.diplomaplugin.entity.test.UserAnswer;
import edu.kpi.iasa.diplomaplugin.repository.test.QuestionRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;

    public UserAnswer saveUserAnswer(String userId, String testId, int questionIndex, int selectedOptionIndex) {
        Question question = questionRepository.findByTestId(testId).get(questionIndex);

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setTestId(testId);
        userAnswer.setQuestionIndex(questionIndex);
        userAnswer.setSelectedOptionIndex(selectedOptionIndex);
        userAnswer.setCorrect(question.getCorrectOptionIndex() == selectedOptionIndex);

        return userAnswerRepository.save(userAnswer);
    }

    public List<UserAnswer> getUserAnswers(String userId, String testId) {
        return userAnswerRepository.findByUserIdAndTestId(userId, testId);
    }

    public int calculateResult(String userId, String testId) {
        List<UserAnswer> answers = userAnswerRepository.findByUserIdAndTestId(userId, testId);
        return (int) answers.stream().filter(UserAnswer::isCorrect).count();
    }
}
