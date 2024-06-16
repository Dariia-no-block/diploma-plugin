package edu.kpi.iasa.diplomaplugin.repository;

import edu.kpi.iasa.diplomaplugin.entity.StudentTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTaskRepository extends MongoRepository<StudentTask, String> {
}
