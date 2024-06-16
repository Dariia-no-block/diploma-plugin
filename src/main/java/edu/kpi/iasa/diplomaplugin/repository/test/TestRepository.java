package edu.kpi.iasa.diplomaplugin.repository.test;

import edu.kpi.iasa.diplomaplugin.entity.test.Test;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TestRepository extends MongoRepository<Test, String> {

}