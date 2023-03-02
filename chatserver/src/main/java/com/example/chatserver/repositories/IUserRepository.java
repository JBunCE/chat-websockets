package com.example.chatserver.repositories;

import com.example.chatserver.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
    Optional<User> findByName(String name);
}
