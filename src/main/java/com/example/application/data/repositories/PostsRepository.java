package com.example.application.data.repositories;

import com.example.application.data.entities.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigInteger;
import java.util.List;

@Repository("postsRepository")
public interface PostsRepository extends CrudRepository<Post, BigInteger> {

    /*SELECT QUERIES*/
    //List<Post> findAll();

    //by one parameter
    List<Post> findAllByUserId(BigInteger userId);
    Post findFirstByPostId(BigInteger postId);


    Post save(Post p);

    Post findByPostIdAndUserId(BigInteger postId, BigInteger userId);

    Post findByOriginalPostIdAndUserId(BigInteger originalPostId, BigInteger userId);
    //ordered
    @Query(value = "select * from POSTS P WHERE P.USER_ID = :userId AND P.POST_ID = :postId ORDER BY POST_DATE DESC", nativeQuery = true)
    List<Post> findAllByPostIdAndUserId(@Param("postId") BigInteger postId,@Param("userId") BigInteger userId);

    //by two parameters
    List<Post> findAllByUserIdAndOriginalPostId(BigInteger userId, BigInteger postId);

    /*DELETE QUERIES*/
    void deleteAllByUserIdAndPostId(BigInteger userId, BigInteger postId);


    //CUSTOM QUERIES
    @Query(value = "select * from POSTS P JOIN FOLLOW F ON (P.USER_ID = F.USER_ID_FOLLOWING) WHERE F.USER_ID_FOLLOWER = :userId ORDER BY POST_DATE DESC", nativeQuery = true)
    List<Post> findAllByUsersFollowedByUserIdOrderByPostDateDesc(@Param("userId") BigInteger userId);

    @Query(value = "SELECT * FROM POSTS WHERE POST_ID IN (WITH PARENT_POST (P) AS (SELECT REPOST_ID AS P FROM POSTS WHERE POST_ID = :postId UNION ALL SELECT REPOST_ID FROM PARENT_POST, POSTS WHERE PARENT_POST.P = POSTS.POST_ID) SELECT * FROM PARENT_POST)", nativeQuery = true)
    List<Post> findPostBranch(@Param("postId") BigInteger postId);

    //List<Post> findAllByUserIdOrderByPostDateDesc(BigInteger userId);
    /**
     * Get all posts.
     * @param pageable Page request.
     * @return List of posts determined by page.
     */
    @Query(value = "select * from POSTS", nativeQuery = true)
    List<Post> findAll(Pageable pageable);

    /**
     * Get posts of users that a certain user follows.
     * @param pageable Page request.
     * @param userId User ID.
     * @return List of posts determined by page.
     */
    @Query(value = "select * from POSTS P JOIN FOLLOW F ON (P.USER_ID = F.USER_ID_FOLLOWING) WHERE F.USER_ID_FOLLOWER = :userId", nativeQuery = true)
    List<Post> findAllByUsersFollowedByUserId(Pageable pageable, @Param("userId") BigInteger userId);

    /**
     * Get all posts by a certain user.
     * @param pageable Page request.
     * @param userId User ID.
     * @return List of posts determined by page.
     */
    @Query(value = "select * from POSTS P WHERE P.USER_ID = :userId", nativeQuery = true)
    List<Post> findAllByUserId(Pageable pageable, BigInteger userId);

    /**
     * Get all posts by a certain user.
     * @param pageable Page request.
     * @param userId User ID.
     * @return List of posts determined by page.
     */
    @Query(value = "select * FROM POSTS P WHERE P.USER_ID IN (SELECT R.RECOMMENDATION_USER_ID FROM RECOMMENDATION R WHERE R.RECOMMENDED_USER_ID = 2 ORDER BY R.SCORE DESC FETCH FIRST 10 ROWS ONLY)", nativeQuery = true)
    List<Post> findAllRecommendedToUserId(Pageable pageable, BigInteger userId);
}
