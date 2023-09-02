package com.example.application.services;

import com.example.application.data.entities.*;
import com.example.application.data.repositories.*;
import com.example.application.exceptions.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InteractionServiceTest {

    @Mock
    private LikesRepository likeRep;
    
    @Mock
    private UsersRepository userRep;
    
    @Mock
    private PostsRepository postRep;
    
    @Mock
    private PostPointLogRepository postPointLogRep;
    
    @Mock
    private UserPointLogRepository userPointLogRep;
    
    @Mock
    private CommentsRepository commentsRep;
    
    @InjectMocks
    private InteractionService interactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsersLiking() {
        // Arrange
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        
        Like like1 = new Like(BigInteger.ONE, BigInteger.ONE);
        Like like2 = new Like(BigInteger.TWO, BigInteger.ONE);
        List<Like> likeEntries = new ArrayList<>();
        likeEntries.add(like1);
        likeEntries.add(like2);
        
        User user1 = new User();
        user1.setUserId(BigInteger.ONE);
        User user2 = new User();
        user2.setUserId(BigInteger.TWO);
        
        when(likeRep.findAllByPostId(post.getPostId())).thenReturn(likeEntries);
        when(userRep.findFirstByUserId(like1.getUserId())).thenReturn(user1);
        when(userRep.findFirstByUserId(like2.getUserId())).thenReturn(user2);

        // Act
        List<User> result = interactionService.getAllUsersLiking(post);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    void testNewLike() {
        // Arrange
        User user = new User();
        user.setUserId(BigInteger.ONE);
        
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        post.setLikes(BigInteger.ZERO);
        
        when(postRep.save(post)).thenReturn(post);

        // Act
        interactionService.newLike(user, post);

        // Assert
        assertEquals(BigInteger.ONE, post.getLikes());
        verify(likeRep, times(1)).save(any(Like.class));
    }

    @Test
    void testDislike() {
        // Arrange
        User user = new User();
        user.setUserId(BigInteger.ONE);
        
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        post.setLikes(BigInteger.ONE);

        Like like = new Like();

        when(likeRep.findByUserIdAndPostId(user.getUserId(), post.getPostId())).thenReturn(like);

        // Act
        interactionService.dislike(user, post);

        // Assert
        assertEquals(BigInteger.ZERO, post.getLikes());
        verify(postRep, times(1)).save(post);
        verify(likeRep, times(1)).delete(like);
    }

    @Test
    void testGetAllReposts() {
        // Arrange
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        post.setOriginalPostId(BigInteger.TWO);
        
        List<Post> reposts = new ArrayList<>();
        reposts.add(new Post());
        reposts.add(new Post());
        
        when(postRep.findAllByOriginalPostId(post.getOriginalPostId())).thenReturn(reposts);

        // Act
        int result = interactionService.getAllReposts(post);

        // Assert
        assertEquals(2, result);
    }

    @Test
    void testIsReposted() {
        // Arrange
        User user = new User();
        user.setUserId(BigInteger.ONE);
        
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        post.setOriginalPostId(BigInteger.TWO);
        
        List<Post> reposts = new ArrayList<>();
        reposts.add(new Post());
        
        when(postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getOriginalPostId())).thenReturn(reposts);
        when(postRep.findAllByUserIdAndOriginalPostId(user.getUserId(), post.getPostId())).thenReturn(reposts);

        // Act & Assert
        assertTrue(interactionService.isReposted(post, user));
    }

    @Test
    void testPointedRepost() {

        PointLogService pointLogService = mock(PointLogService.class);
        interactionService.postPointLogService = pointLogService;

        // Arrange
        User newUser = new User();
        newUser.setUserId(BigInteger.valueOf(4));
        newUser.setType1Points(BigInteger.ZERO);
        newUser.setType2Points(BigInteger.valueOf(10));

        User directUser = new User();
        directUser.setUserId(BigInteger.ONE);
        directUser.setType1Points(BigInteger.ZERO);

        User intermidiateUser = new User();
        intermidiateUser.setUserId(BigInteger.TWO);
        intermidiateUser.setType1Points(BigInteger.valueOf(10));

        User originalUser = new User();
        originalUser.setUserId(BigInteger.valueOf(3));
        originalUser.setType1Points(BigInteger.valueOf(15));

        Post newRepost = new Post();
        newRepost.setPostId(BigInteger.valueOf(4));
        newRepost.setUserId(newUser.getUserId());
        newRepost.setPoints(BigInteger.ZERO);
        newRepost.setRepostId(BigInteger.ONE);
        newRepost.setOriginalPostId(BigInteger.valueOf(3));

        Post directPost = new Post();
        directPost.setPostId(BigInteger.ONE);
        directPost.setUserId(directUser.getUserId());
        directPost.setPoints(BigInteger.ZERO);
        directPost.setRepostId(BigInteger.TWO);
        directPost.setOriginalPostId(BigInteger.valueOf(3));

        Post intermidiatePost = new Post();
        intermidiatePost.setPostId(BigInteger.TWO);
        intermidiatePost.setUserId(intermidiateUser.getUserId());
        intermidiatePost.setPoints(BigInteger.valueOf(10));
        intermidiatePost.setRepostId(BigInteger.TWO);
        intermidiatePost.setOriginalPostId(BigInteger.valueOf(3));

        Post original = new Post();
        original.setPostId(BigInteger.valueOf(3));
        original.setUserId(originalUser.getUserId());
        original.setPoints(BigInteger.valueOf(15));

        List<Post> branch = new ArrayList<>();
        branch.add(directPost);
        branch.add(intermidiatePost);
        branch.add(original);

        when(postRep.findPostBranch(newRepost.getPostId())).thenReturn(branch);

        when(userRep.findFirstByUserId(directUser.getUserId())).thenReturn(directUser);
        when(userRep.findFirstByUserId(intermidiateUser.getUserId())).thenReturn(intermidiateUser);
        when(userRep.findFirstByUserId(originalUser.getUserId())).thenReturn(originalUser);

        when(pointLogService.findLastInsertedPostLog()).thenReturn(new PostsPointLog());
        when(pointLogService.findLastInsertedUserLog()).thenReturn(new UserPointLog());
        
        when(postRep.save(any(Post.class))).thenReturn(newRepost);
        
        // Act
        assertDoesNotThrow(() -> interactionService.pointedRepost(directPost, newUser));
        
        // Assert
        assertEquals(BigInteger.valueOf(5), newUser.getType2Points());
        verify(postRep, times(4)).save(any(Post.class));
        verify(userRep, times(1)).save(newUser);
        verify(userRep, times(2)).save(directUser);
        verify(userRep, times(1)).save(intermidiateUser);
        verify(userRep, times(2)).save(originalUser);
    }

    @Test
    void testPointedRepost_NotEnoughPoints() {
        // Arrange
        User user = new User();
        user.setUserId(BigInteger.ONE);
        user.setType2Points(BigInteger.valueOf(4));
        
        Post post = new Post();
        post.setPostId(BigInteger.ONE);

        // Act & Assert
        assertThrows(PostException.class, () -> interactionService.pointedRepost(post, user));
        assertEquals(BigInteger.valueOf(4), user.getType2Points());
        verify(postRep, times(0)).save(any(Post.class));
        verify(userRep, times(0)).save(user);
    }

    @Test
    void testNotPointedRepost() {
        // Arrange
        User user = new User();
        user.setUserId(BigInteger.ONE);
        
        Post post = new Post();
        post.setPostId(BigInteger.ONE);
        
        when(postRep.save(any(Post.class))).thenReturn(post);

        // Act
        interactionService.notPointedRepost(post, user);

        // Assert
        verify(postRep, times(1)).save(new Post(post, user, false));
    }

    @Test
    void testPointDistribution() {
        // Arrange

        PointLogService pointLogService = mock(PointLogService.class);
        interactionService.postPointLogService = pointLogService;

        User directUser = new User();
        directUser.setUserId(BigInteger.ONE);
        directUser.setType1Points(BigInteger.ZERO);

        User intermidiateUser = new User();
        intermidiateUser.setUserId(BigInteger.TWO);
        intermidiateUser.setType1Points(BigInteger.valueOf(10));

        User originalUser = new User();
        originalUser.setUserId(BigInteger.valueOf(3));
        originalUser.setType1Points(BigInteger.valueOf(15));


        Post directPost = new Post();
        directPost.setPostId(BigInteger.ONE);
        directPost.setUserId(directUser.getUserId());
        directPost.setPoints(BigInteger.ZERO);
        directPost.setRepostId(BigInteger.TWO);
        directPost.setOriginalPostId(BigInteger.valueOf(3));

        Post intermidiatePost = new Post();
        intermidiatePost.setPostId(BigInteger.TWO);
        intermidiatePost.setUserId(intermidiateUser.getUserId());
        intermidiatePost.setPoints(BigInteger.valueOf(10));
        intermidiatePost.setRepostId(BigInteger.TWO);
        intermidiatePost.setOriginalPostId(BigInteger.valueOf(3));

        Post original = new Post();
        original.setPostId(BigInteger.valueOf(3));
        original.setUserId(originalUser.getUserId());
        original.setPoints(BigInteger.valueOf(15));
        
        List<Post> branch = new ArrayList<>();
        branch.add(directPost);
        branch.add(intermidiatePost);
        branch.add(original);

        when(postRep.findPostBranch(directPost.getPostId())).thenReturn(branch);

        when(userRep.findFirstByUserId(directUser.getUserId())).thenReturn(directUser);
        when(userRep.findFirstByUserId(intermidiateUser.getUserId())).thenReturn(intermidiateUser);
        when(userRep.findFirstByUserId(originalUser.getUserId())).thenReturn(originalUser);

        when(pointLogService.findLastInsertedPostLog()).thenReturn(new PostsPointLog());
        when(pointLogService.findLastInsertedUserLog()).thenReturn(new UserPointLog());

        // Act
        interactionService.pointDistribution(directPost, directUser);
        
        // Assert

        assertEquals(BigInteger.valueOf(10), directPost.getPoints());
        assertEquals(BigInteger.valueOf(10), directUser.getType1Points());

        assertEquals(BigInteger.valueOf(15), intermidiatePost.getPoints());
        assertEquals(BigInteger.valueOf(15), intermidiateUser.getType1Points());

        assertEquals(BigInteger.valueOf(30), original.getPoints());
        assertEquals(BigInteger.valueOf(30), originalUser.getType1Points());

        verify(postRep, times(3)).save(any(Post.class));
        verify(userRep, times(2)).save(directUser);
        verify(userRep, times(1)).save(intermidiateUser);
        verify(userRep, times(2)).save(originalUser);

    }
}
