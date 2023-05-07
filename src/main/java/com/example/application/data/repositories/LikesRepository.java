package com.example.application.data.repositories;

import com.example.application.data.entities.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("likesRepository")
public interface LikesRepository extends CrudRepository<Like, BigInteger> {

    List<Like> findAllByPostId(BigInteger postId);
    Like findByUserIdAndPostId(BigInteger userId, BigInteger postId);
    void deleteAllByUserIdAndPostId(BigInteger userId, BigInteger postId);
}
