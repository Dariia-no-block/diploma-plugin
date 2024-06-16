package edu.kpi.iasa.diplomaplugin.repository;

import edu.kpi.iasa.diplomaplugin.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByDiscordId(String discordId);
}
