package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.application.data.entities.*;

import java.math.BigInteger;
import java.util.List;

@Repository("postPointLogRepository")
public interface PostPointLogRepository extends CrudRepository<PostsPointLog, BigInteger>  {

    List<PostsPointLog> findAllByPostIdOrderByLogDateDesc (BigInteger postId);

    @Query(value = "select * from POSTS_POINT_LOGS WHERE LOG_ID IN (SELECT MAX(LOG_ID) FROM POSTS_POINT_LOGS)", nativeQuery = true)
    PostsPointLog findLastInsertedLog();
}
