package com.example.application.services;

import com.example.application.data.entities.Follow;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.data.repositories.RecommendationRepository;
import com.example.application.data.repositories.UserPointLogRepository;
import com.example.application.data.repositories.UsersRepository;
import com.example.application.exceptions.UserException;
import com.example.application.services.DropboxService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserPointLogRepository userPointLogRepository;

    @Mock
    private DropboxService dropboxService;

    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, followRepository, recommendationRepository, userPointLogRepository);
        userService.dropboxService = dropboxService;
    }

    @Test
    public void testFindById() {
        // Mocking data
        BigInteger userId = BigInteger.valueOf(1);
        User user = new User();
        when(userRepository.findFirstByUserId(userId)).thenReturn(user);

        // Test
        User result = userService.findById(userId);

        // Verify
        assertEquals(user, result);
        verify(userRepository, times(1)).findFirstByUserId(userId);
    }

    @Test
    public void testFindByUsername() {
        // Mocking data
        String username = "john";
        User user = new User();
        when(userRepository.findFirstByUsername(username)).thenReturn(user);

        // Test
        User result = userService.findByUsername(username);

        // Verify
        assertEquals(user, result);
        verify(userRepository, times(1)).findFirstByUsername(username);
    }

    @Test
    public void testFindByEmail() {
        // Mocking data
        String email = "john@example.com";
        User user = new User();
        when(userRepository.findFirstByEmail(email)).thenReturn(user);

        // Test
        User result = userService.findByEmail(email);

        // Verify
        assertEquals(user, result);
        verify(userRepository, times(1)).findFirstByEmail(email);
    }

    @Test
    public void testGetProfilePicBytes() {
        // Mocking data
        User user = new User();
        byte[] profilePictureBytes = "profile picture".getBytes();
        when(dropboxService.downloadProfilePicture(user)).thenReturn(profilePictureBytes);

        // Test
        byte[] result = userService.getProfilePicBytes(user);

        // Verify
        assertEquals(profilePictureBytes, result);
        verify(dropboxService, times(1)).downloadProfilePicture(user);
    }

    @Test
    public void testFollow() {
        // Mocking data
        User follower = new User();
        User following = new User();

        // Test
        userService.follow(follower, following);

        // Verify
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    public void testUnfollow() {
        // Mocking data
        User follower = new User();
        User following = new User();
        follower.setUserId(new BigInteger("1000"));
        following.setUserId(new BigInteger("1001"));
        Follow follow = new Follow();
        when(followRepository.findByUserIdFollowerAndUserIdFollowing(new BigInteger("1000"), new BigInteger("1001"))).thenReturn(follow);

        // Test
        userService.unfollow(follower, following);

        // Verify
        verify(followRepository, times(1)).delete(follow);
    }

    @Test
    public void testSave() {
        // Mocking data
        User user = new User();


        // Test
        User result = userService.save(user);

        // Verify
        assertEquals(user, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testFindAllByMatchingUsername() {
        // Mocking data
        String match = "john";
        List<User> users = Collections.singletonList(new User());
        when(userRepository.findAllByUsernameContainsIgnoreCase(match)).thenReturn(users);

        // Test
        List<User> result = userService.findAllByMatchingUsername(match);

        // Verify
        assertEquals(users, result);
        verify(userRepository, times(1)).findAllByUsernameContainsIgnoreCase(match);
    }

    @Test
    public void testChangeProfilePicture() throws UserException, IOException {
        // Mocking data
        User user = new User();
        MockMultipartFile file = new MockMultipartFile("profile.jpg", "profile.jpg", "image/jpeg", "image data".getBytes());

        // Test
        userService.changeProfilePicture(user, file.getInputStream());

        // Verify
        verify(dropboxService, times(1)).uploadProfilePicture(eq(user), any(InputStream.class));
    }

    // Add more test methods for other methods in the UserService class

}
