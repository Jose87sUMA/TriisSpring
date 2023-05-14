package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("usersRepository")
public interface UsersRepository extends CrudRepository<User, BigInteger> {

    User findFirstByUserId(BigInteger userId);
    User findFirstByUsername(String username);

    @Query(value = "select * from USERS ORDER BY TYPE_1_POINTS DESC FETCH FIRST 10 ROWS ONLY", nativeQuery = true)
    List<User> findTenOrderByType1PointsDesc();

}
