package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.PostsPointLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("postPointLogRepository")

public interface PostPointLogRepository extends CrudRepository<PostsPointLog, BigInteger>  {

    List<PostsPointLog> findAllByPostIdOrderByLogDateDesc (BigInteger postId);

}
