package com.cm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cm.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    //Extera methods can be defined here if needed
    //create custom query methods if needed
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByEmailToken(String emailToken);


    
}
