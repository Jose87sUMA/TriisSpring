package com.example.application.data.repositories;

import com.example.application.data.entities.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("commentsRepository")
public interface CommentsRepository extends CrudRepository<Comment, BigInteger> {

    List<Comment> findAllByPostId(BigInteger postId);
    List<Comment> findAllByUserIdAndPostIdOrderByCommentDateDesc(BigInteger userId, BigInteger postId);


}
