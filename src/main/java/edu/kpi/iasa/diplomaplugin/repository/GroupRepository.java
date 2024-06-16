package edu.kpi.iasa.diplomaplugin.repository;

import edu.kpi.iasa.diplomaplugin.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

	Optional<Group> findByName(String name);

}
