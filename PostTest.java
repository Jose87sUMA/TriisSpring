import com.example.application.data.entities.Post;

import java.math.BigInteger;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {
    @org.junit.jupiter.api.Test
    void testEquals() {
        Post tester = new Post();
        Date date = new Date(2000, 12, 13);
        BigInteger A = new BigInteger("3265");
        tester.setPostId(A);
        tester.setUserId(A);
        tester.setPostDate(date);
        tester.setPoints(A);
        tester.setLikes(A);
        tester.setPointed("laba");
        tester.setRepostId(A);
        tester.setOriginalPostId(A);
        Post tester2 = new Post();
        tester2.setPostId(A);
        tester2.setUserId(A);
        tester2.setPostDate(date);
        tester2.setPoints(A);
        tester2.setLikes(A);
        tester2.setPointed("laba");
        tester2.setRepostId(A);
        tester2.setOriginalPostId(A);
        assertEquals(true, tester.equals(tester2), "Should be equal");
    }
    @org.junit.jupiter.api.Test
    void testGetPostId() {
        Post tester = new Post();
        BigInteger A = new BigInteger("696969");
        tester.setPostId(A);
        assertEquals(A, tester.getPostId(), "we set PostId to 696969");
    }

    @org.junit.jupiter.api.Test
    void testSetPostId() {
        Post tester = new Post();
        BigInteger A = new BigInteger("969696");
        tester.setPostId(A);
        assertEquals(A, tester.getPostId(), "we set PostId to 696969");
    }

    @org.junit.jupiter.api.Test
    void testGetUserId() {
        Post tester = new Post();
        BigInteger A = new BigInteger("568956");
        tester.setUserId(A);
        assertEquals(A, tester.getUserId(), "we set UserId to 568956");
    }

    @org.junit.jupiter.api.Test
    void testSetUserId() {
        Post tester = new Post();
        BigInteger A = new BigInteger("758926");
        tester.setUserId(A);
        assertEquals(A, tester.getUserId(), "we set UserId to 758926");
    }

    @org.junit.jupiter.api.Test
    void testGetPostDate() {
        Date date = new Date(2000, 12, 13);
        Post tester = new Post();
        tester.setPostDate(date);
        assertEquals(date, tester.getPostDate(), "we set the date to 13/12/2000");
    }

    @org.junit.jupiter.api.Test
    void testSetPostDate() {
        Date date = new Date(2034, 6, 20);
        Post tester = new Post();
        tester.setPostDate(date);
        assertEquals(date, tester.getPostDate(), "we set the date to 20/6/2034");
    }

    @org.junit.jupiter.api.Test
    void testGetPoints() {
        Post tester = new Post();
        BigInteger A = new BigInteger("645");
        tester.setPoints(A);
        assertEquals(A, tester.getPoints(), "we set PostId to 645");
    }

    @org.junit.jupiter.api.Test
    void testSetPoints() {
        Post tester = new Post();
        BigInteger A = new BigInteger("356");
        tester.setPoints(A);
        assertEquals(A, tester.getPoints(), "we set PostId to 356");
    }

    @org.junit.jupiter.api.Test
    void testGetLikes() {
        Post tester = new Post();
        BigInteger A = new BigInteger("56");
        tester.setLikes(A);
        assertEquals(A, tester.getLikes(), "we set PostId to 56");
    }

    @org.junit.jupiter.api.Test
    void testSetLikes() {
        Post tester = new Post();
        BigInteger A = new BigInteger("71");
        tester.setLikes(A);
        assertEquals(A, tester.getLikes(), "we set PostId to 71");
    }
}