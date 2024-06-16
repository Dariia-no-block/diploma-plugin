package edu.kpi.iasa.diplomaplugin.service;


import edu.kpi.iasa.diplomaplugin.entity.test.AssignedTest;
import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import edu.kpi.iasa.diplomaplugin.entity.test.Test;
import edu.kpi.iasa.diplomaplugin.entity.test.UserAnswer;
import edu.kpi.iasa.diplomaplugin.repository.test.AssignedTestRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.QuestionRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.TestRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AssignedTestRepository assignedTestRepository;
    private final UserAnswerRepository userAnswerRepository;

    private final Map<String, String> selectedTestIds = new ConcurrentHashMap<>();
    private final Map<String, String> selectedGroupIds = new ConcurrentHashMap<>();

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public void setSelectedTestId(String userId, String testId) {
        selectedTestIds.put(userId, testId);
    }

    public String getSelectedTestId(String userId) {
        return selectedTestIds.get(userId);
    }
    public Test createTest(Test test) {
        return testRepository.save(test);
    }

    public void setSelectedGroupId(String userId, String groupId) {
        selectedGroupIds.put(userId, groupId);
    }

    public String getSelectedGroupId(String userId) {
        return selectedGroupIds.get(userId);
    }

    public Question addQuestion(Question question) {
        Question savedQuestion = questionRepository.save(question);
        Optional<Test> optionalTest = testRepository.findById(savedQuestion.getTestId());
        if (optionalTest.isPresent()) {
            Test test = optionalTest.get();
            test.getQuestionIds().add(savedQuestion.getId());
            testRepository.save(test);
        }
        return savedQuestion;
    }

    public AssignedTest assignTest(String testId, String groupId, long startTime, long endTime) {
        AssignedTest assignedTest = new AssignedTest();
        assignedTest.setTestId(testId);
        assignedTest.setGroupId(groupId);
        assignedTest.setStartTime(startTime);
        assignedTest.setEndTime(endTime);
        return assignedTestRepository.save(assignedTest);
    }

    public List<Question> getQuestionsByTestId(String testId) {
        return questionRepository.findByTestId(testId);
    }

    public void deleteAll(){
        testRepository.deleteAll();
    }

    public List<AssignedTest> getAllAssignTest(){
        return assignedTestRepository.findAll();
    }

    public List<UserAnswer> getResults(){
        return userAnswerRepository.findAll();
    }

}
