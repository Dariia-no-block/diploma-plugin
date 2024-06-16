package edu.kpi.iasa.diplomaplugin.repository.test;


import edu.kpi.iasa.diplomaplugin.entity.test.UserAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends MongoRepository<UserAnswer, String> {
    List<UserAnswer> findByUserIdAndTestId(String userId, String testId);
}
