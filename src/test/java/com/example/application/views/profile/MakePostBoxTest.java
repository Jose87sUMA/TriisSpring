package com.example.application.views.profile;


import com.example.application.data.entities.User;
import com.example.application.exceptions.MakePostException;
import com.example.application.services.MakePostService;
import com.example.application.services.PostService;
import com.example.application.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class MakePostBoxTest {

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MakePostService makePostService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void subtractPointsFromUser_ShouldSubtractPointsFromUser() {
        // Arrange
        User user = new User();
        user.setType1Points(new BigInteger("10"));

        // Act
        makePostService.subtractPointsFromUser(user, makePostService.getPostCost(user));

        // Assert
        assertEquals(new BigInteger("0"), user.getType1Points());
        verify(userService, times(1)).save(user);
    }

    @Test
    void checkEnoughPointsForPost_WhenEnoughPoints_ReturnsTrue() {
        // Arrange
        User user = new User();
        user.setType1Points(new BigInteger("10"));

        // Act
        boolean result = makePostService.checkEnoughPointsForPost(user);

        // Assert
        assertTrue(result);
    }

    @Test
    void checkEnoughPointsForPost_WhenNotEnoughPoints_ReturnsFalse() {
        // Arrange
        User user = new User();
        user.setType1Points(new BigInteger("5"));

        // Act
        boolean result = makePostService.checkEnoughPointsForPost(user);

        // Assert
        assertFalse(result);
    }

    @Test
    void validateFileContent_WhenValidFileData_DoesNotThrowException() {
        // Arrange
        InputStream fileData = new ByteArrayInputStream("Test data".getBytes());

        // Act and Assert
        assertDoesNotThrow(() -> makePostService.validateFileContent(fileData));
    }

    @Test
    void validateFileContent_WhenEmptyFileData_ThrowsMakePostException() {
        // Arrange
        InputStream fileData = null;
        try {
            fileData = new FileInputStream("");
        } catch (FileNotFoundException e) {

        }

        // Act and Assert
        InputStream finalFileData = fileData;
        assertThrows(MakePostException.class, () -> makePostService.validateFileContent(finalFileData));
    }

    @Test
    void validateFileContent_WhenNullFileData_ThrowsMakePostException() {
        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.validateFileContent(null));
    }

    @Test
    void manageImageURL_WhenValidLink_ReturnsInputStream() throws IOException {
        // Arrange
        String link = "https://www.adslzone.net/app/uploads-adslzone.net/2019/04/borrar-fondo-imagen-1200x675.jpg";

        // Act
        InputStream result = makePostService.manageImageURL(link);

        // Assert
        assertNotNull(result);
    }

    @Test
    void manageImageURL_WhenEmptyLink_ThrowsMakePostException() {
        // Arrange
        String link = "";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.manageImageURL(link));
    }

    @Test
    void manageImageURL_WhenInvalidLink_ThrowsMakePostException() {
        // Arrange
        String link = "https://example.com/invalid";

        // Act and Assert
        assertThrows(MakePostException.class, () -> makePostService.manageImageURL(link));
    }
}