package com.example.application.data.repositories;

import com.example.application.data.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository("usersRepository")
public interface UsersRepository extends CrudRepository<User, BigInteger> {

    User findFirstByUserId(BigInteger userId);
    User findFirstByUsername(String username);

}
