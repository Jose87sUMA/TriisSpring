package com.example.application.data.entities;

import com.vaadin.flow.component.html.Image;
import jakarta.persistence.*;

import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "POSTS", schema = "UBD3336", catalog = "")
public class Post implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "POST_ID")
    private BigInteger postId;
    @Basic
    @Column(name = "USER_ID")
    private BigInteger userId;
    @Basic
    @Column(name = "POST_DATE")
    private Date postDate;
    @Basic
    @Column(name = "POINTS")
    private BigInteger points;
    @Basic
    @Column(name = "LIKES")
    private BigInteger likes;
    @Basic
    @Column(name = "CONTENT")
    private String content;
    @Basic
    @Column(name = "POINTED")
    private String pointed;
    @Basic
    @Column(name = "REPOST_ID")
    private BigInteger repostId;
    @Basic
    @Column(name = "ORIGINAL_POST_ID")
    private BigInteger originalPostId;


    public Post() {
    }

    //FOR POST
    public Post(User user, boolean pointed) {

        this.postId = null;
        this.originalPostId = null;
        this.repostId = null;
        this.postDate = Date.from(Instant.now());
        this.userId = user.getUserId();
        this.points = BigInteger.ZERO;
        this.pointed = pointed ? "Y":"N";
        this.likes = BigInteger.ZERO;
    }






    public BigInteger getPostId() {
        return postId;
    }

    public void setPostId(BigInteger postId) {
        this.postId = postId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public BigInteger getPoints() {
        return points;
    }

    public void setPoints(BigInteger points) {
        this.points = points;
    }

    public BigInteger getLikes() {
        return likes;
    }

    public void setLikes(BigInteger likes) {
        this.likes = likes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPointed() {
        return pointed;
    }

    public void setPointed(String pointed) {
        this.pointed = pointed;
    }

    public BigInteger getRepostId() {
        return repostId;
    }

    public void setRepostId(BigInteger repostId) {
        this.repostId = repostId;
    }

    public BigInteger getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(BigInteger originalPostId) {
        this.originalPostId = originalPostId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post that = (Post) o;
        return Objects.equals(postId, that.postId) && Objects.equals(userId, that.userId) && Objects.equals(postDate, that.postDate) && Objects.equals(points, that.points) && Objects.equals(likes, that.likes) && Objects.equals(content, that.content) && Objects.equals(pointed, that.pointed) && Objects.equals(repostId, that.repostId) && Objects.equals(originalPostId, that.originalPostId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(postId, userId, postDate, points, likes, pointed, repostId, originalPostId, content);
        result = 31 * result;
        return result;
    }

    public static byte[] getBlobFromInputStream(InputStream inputStream){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while (true) {
            try {
                if (!((bytesRead = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] bytes = outputStream.toByteArray();
        return bytes;
    }


}
