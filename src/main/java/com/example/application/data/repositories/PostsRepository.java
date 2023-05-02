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

    Post findByRepostIdAndUserId(BigInteger repostId, BigInteger userId);
    //ordered
    List<Post> findAllByPostIdAndUserIdOrderByPostDateDesc(BigInteger postId, BigInteger userId);

    //by two parameters
    List<Post> findAllByUserIdAndOriginalPostId(BigInteger userId, BigInteger postId);

    /*DELETE QUERIES*/
    void deleteAllByUserIdAndPostId(BigInteger userId, BigInteger postId);
    @Query(value = "select * from POSTS P JOIN FOLLOW F ON (P.USER_ID = F.USER_ID_FOLLOWING) WHERE F.USER_ID_FOLLOWER = :userId ORDER BY POST_DATE DESC", nativeQuery = true)
    List<Post> findAllByUsersFollowedByUserIdOrderByPostDateDesc(@Param("userId") BigInteger userId);

}
