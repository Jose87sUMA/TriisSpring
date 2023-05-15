package com.example.application.data.repositories;

import com.example.application.data.entities.Follow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("followRepository")
public interface FollowRepository extends CrudRepository<Follow, BigInteger> {

    List<Follow> findAllByUserIdFollower(BigInteger UserIdFollower);
    List<Follow> findAllByUserIdFollowing(BigInteger UserIdFollowing);
    Follow findByUserIdFollowerAndUserIdFollowing(BigInteger userId, BigInteger userId1);
}
