package edu.kpi.iasa.diplomaplugin.repository.test;

import edu.kpi.iasa.diplomaplugin.entity.test.AssignedTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignedTestRepository extends MongoRepository<AssignedTest, String> {
    List<AssignedTest> findByGroupId(String groupId);
    Optional<AssignedTest> findByTestIdAndGroupIdAndStudentsPassedTestListContaining(String testId, String groupId, String studentId);
}