package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository("postsRepository")
public interface PostsRepository extends CrudRepository<Post, BigInteger> {

    /*SELECT QUERIES*/
    //by one parameter
    List<Post> findAllByUserId(BigInteger userId);
    Post findFirstByPostId(BigInteger postId);

    Post save(Post p);

    Post findByPostIdAndUserId(BigInteger postId, BigInteger userId);

    Post findByOriginalPostIdAndUserId(BigInteger originalPostId, BigInteger userId);
    //ordered
    List<Post> findAllByPostIdAndUserIdOrderByPostDateDesc(BigInteger postId, BigInteger userId);

    //by two parameters
    List<Post> findAllByUserIdAndOriginalPostId(BigInteger userId, BigInteger postId);

    /*DELETE QUERIES*/
    void deleteAllByUserIdAndPostId(BigInteger userId, BigInteger postId);


    //CUSTOM QUERIES
    @Query(value = "select * from POSTS P JOIN FOLLOW F ON (P.USER_ID = F.USER_ID_FOLLOWING) WHERE F.USER_ID_FOLLOWER = :userId ORDER BY POST_DATE DESC", nativeQuery = true)
    List<Post> findAllByUsersFollowedByUserIdOrderByPostDateDesc(@Param("userId") BigInteger userId);

    @Query(value = "SELECT * FROM POSTS WHERE POST_ID IN (WITH PARENT_POST (P) AS (SELECT REPOST_ID AS P FROM POSTS WHERE POST_ID = :postId UNION ALL SELECT REPOST_ID FROM PARENT_POST, POSTS WHERE PARENT_POST.P = POSTS.POST_ID) SELECT * FROM PARENT_POST)", nativeQuery = true)
    List<Post> findPostBranch(@Param("postId") BigInteger postId);

}
