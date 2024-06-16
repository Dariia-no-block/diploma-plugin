package edu.kpi.iasa.diplomaplugin.repository.test;

import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByTestId(String testId);
}