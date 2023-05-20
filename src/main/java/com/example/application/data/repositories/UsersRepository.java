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
            "where lower(c.username) like lower(concat('%', :searchTerm, '%'))" + "and c IN(:userFollowing)")
    List<User> searchFollowing(@Param("searchTerm") String searchTerm,@Param("userFollowing") List<User> userFollowing );

    @Query("select c from User c " +
            "where lower(c.username) like lower(concat('%', :searchFilter, '%'))"  + "and c IN(:userFollower)")
    List<User> searchFollowers(@Param("searchFilter")String stringFilter,@Param("userFollower") List<User> userFollower );


    @Query("select c from User c "+
            "where lower(c.username) like lower(concat('%', :filterSearch, '%'))"  )
    List<User> searchUsers(@Param("filterSearch")String filterSearch);





}
