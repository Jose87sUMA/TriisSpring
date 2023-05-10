package com.example.application.data.repositories;

import com.example.application.data.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("usersRepository")
public interface UsersRepository extends CrudRepository<User, BigInteger> {

    User findFirstByUserId(BigInteger userId);
    User findFirstByUsername(String username);
    User findFirstByEmail(String email);

    List<User> findAllByUsernameContainsIgnoreCase(String match);

    @Query("select c from User c " +
            "where lower(c.username) like lower(concat('%', :searchTerm, '%'))")

    List<User> searchFollowing(@Param("searchTerm") String searchTerm);

    @Query("select c from User c " +
            "where lower(c.username) like lower(concat('%', :searchFilter, '%'))")
    List<User> searchFollowers(@Param("searchFilter")String stringFilter);

}
