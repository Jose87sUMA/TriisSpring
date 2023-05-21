package com.example.application.services;

import com.example.application.data.entities.Post;
import com.example.application.data.entities.User;
import com.example.application.data.repositories.FollowRepository;
import com.example.application.exceptions.MakePostException;
import com.example.application.services.MakePostService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class MakePostServiceTest {

    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private FollowRepository followRepository;

    private MakePostService makePostService;
    User user;

    @BeforeEach
    void setUp() {
        user = new User("Tester", "password","mail");
        makePostService = new MakePostService();
        makePostService.setUserService(userService);
        makePostService.setPostService(postService);
    }

    @Test
    void postPointedByFile_WhenValidFileDataAndEnoughPoints_ShouldReturnPost() throws MakePostException, IOException {
        // Arrange
        User user = new User();
        user.setType1Points(new BigInteger("100")); // Set the type1Points value to a non-null BigInteger

        InputStream fileData = new ByteArrayInputStream("Test data".getBytes());

        // Mock the behavior of userService.getNumberOfFollowers(user) method
        when(userService.getNumberOfFollowers(any(User.class))).thenReturn(10);

        // Mock the behavior of postService.createPost() method
        Post expectedPost = new Post();
        when(postService.createPost(any(User.class), eq(true), any(InputStream.class))).thenReturn(expectedPost);

        // Act
        Post actualPost = makePostService.postPointedByFile(user, fileData);

        // Assert
        assertEquals(expectedPost, actualPost);
    }

    @Test
    void postPointedByFile_WhenValidFileDataAndNotEnoughPoints_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = new ByteArrayInputStream("Test data".getBytes());
        user.setType1Points(new BigInteger("5"));
        when(userService.getNumberOfFollowers(any(User.class))).thenReturn(10);

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postPointedByFile(user, fileData));
        verify(userService, times(0)).save(user);
    }

    @Test
    void postPointedByFile_WhenInvalidFileData_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = null;

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postPointedByFile(user, fileData));
        verify(userService, times(0)).save(user);
    }

    @Test
    void postNotPointedByFile_WhenValidFileData_ShouldReturnPost() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = new ByteArrayInputStream("Test data".getBytes());
        Post expectedPost = new Post();

        when(postService.createPost(user, false, fileData)).thenReturn(expectedPost);

        // Act
        Post actualPost = makePostService.postNotPointedByFile(user, fileData);

        // Assert
        assertEquals(expectedPost, actualPost);
    }

    @Test
    void postNotPointedByFile_WhenInvalidFileData_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = null;

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postNotPointedByFile(user, fileData));
    }

    @Test
    void postPointedByLink_WhenValidLink_ShouldReturnPost() throws MakePostException, IOException {
        // Arrange
        String link = "https://img.freepik.com/premium-photo/image-colorful-galaxy-sky-generative-ai_791316-9864.jpg?w=2000";
        Post expectedPost = new Post();

        when(postService.createPost(eq(user), eq(true), any(InputStream.class))).thenReturn(expectedPost);

        // Act
        Post actualPost = makePostService.postPointedByLink(user, link);


        // Assert
        assertEquals(expectedPost, actualPost);
        verify(userService, times(1)).save(user);
    }

    @Test
    void postPointedByLink_WhenEmptyLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postPointedByLink(user, link));
        verify(userService, times(0)).save(user);
    }

    @Test
    void postPointedByLink_WhenInvalidLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "invalid-link";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postPointedByLink(user, link));
        verify(userService, times(0)).save(user);
    }

    @Test
    void postNotPointedByLink_WhenValidLink_ShouldReturnPost() throws MakePostException, IOException {
        // Arrange
        String link = "https://img.freepik.com/premium-photo/image-colorful-galaxy-sky-generative-ai_791316-9864.jpg?w=2000";
        Post expectedPost = new Post();

        when(postService.createPost(eq(user), eq(false), any(InputStream.class))).thenReturn(expectedPost);

        // Act
        Post actualPost = makePostService.postNotPointedByLink(user, link);

        // Assert
        assertEquals(expectedPost, actualPost);

    }

    @Test
    void postNotPointedByLink_WhenEmptyLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postNotPointedByLink(user, link));
    }

    @Test
    void postNotPointedByLink_WhenInvalidLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "invalid-link";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.postNotPointedByLink(user, link));
    }

    @Test
    void subtractPointsFromUser_ShouldUpdateUserPoints() {
        // Arrange
        BigInteger initialPoints = new BigInteger("100");
        BigInteger pointsToSubtract = new BigInteger("50");
        user.setType1Points(initialPoints);

        // Act
        makePostService.subtractPointsFromUser(user, pointsToSubtract);

        // Assert
        BigInteger expectedPoints = initialPoints.subtract(pointsToSubtract);
        assertEquals(expectedPoints, user.getType1Points());
        verify(userService, times(1)).save(user);
    }

    @Test
    void checkEnoughPointsForPost_WhenEnoughPoints_ShouldReturnTrue() {
        // Arrange
        int numberOfFollowers = 10;
        user.setType1Points(new BigInteger("100"));
        doReturn(numberOfFollowers).when(userService).getNumberOfFollowers(user);

        // Act
        boolean result = makePostService.checkEnoughPointsForPost(user);

        // Assert
        assertTrue(result);
    }

    @Test
    void checkEnoughPointsForPost_WhenNotEnoughPoints_ShouldReturnFalse() {
        // Arrange
        BigInteger userPoints = new BigInteger("20");
        when(userService.getNumberOfFollowers(any(User.class))).thenReturn(10);
        user.setType1Points(userPoints);

        // Act
        boolean result = makePostService.checkEnoughPointsForPost(user);

        // Assert
        assertFalse(result);
    }

    @Test
    void validateFileContent_WhenValidFileData_ShouldNotThrowException() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = new ByteArrayInputStream("Test data".getBytes());

        // Act and Assert
        assertDoesNotThrow(() -> makePostService.validateFileContent(fileData));
    }

    @Test
    void validateFileContent_WhenInvalidFileData_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        InputStream fileData = null;

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.validateFileContent(fileData));
    }

    @Test
    void manageImageURL_WhenValidLink_ShouldReturnInputStream() throws MakePostException, IOException {
        // Arrange
        String link = "https://img.freepik.com/premium-photo/image-colorful-galaxy-sky-generative-ai_791316-9864.jpg?w=2000";
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        InputStream expectedInputStream = connection.getInputStream();

        // Act
        InputStream actualInputStream = makePostService.manageImageURL(link);

        // Assert
        assertArrayEquals(expectedInputStream.readAllBytes(), actualInputStream.readAllBytes());
    }

    @Test
    void manageImageURL_WhenEmptyLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.manageImageURL(link));
    }

    @Test
    void manageImageURL_WhenInvalidLink_ShouldThrowMakePostException() throws MakePostException, IOException {
        // Arrange
        String link = "invalid-link";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.manageImageURL(link));
    }

    @Test
    void getPostCost_ShouldReturnCorrectCost() {
        // Arrange
        int numberOfFollowers = 10;
        int maxCost = 30000;

        when(userService.getNumberOfFollowers(user)).thenReturn(numberOfFollowers);

        // Act
        BigInteger actualCost = makePostService.getPostCost(user);

        // Assert
        BigInteger expectedCost = BigInteger.valueOf(Math.min(numberOfFollowers * 3, maxCost));
        assertEquals(expectedCost, actualCost);
    }

}
