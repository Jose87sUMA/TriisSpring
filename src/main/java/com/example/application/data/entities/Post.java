package com.example.application.data.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;

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
    private Date post_date;
    @Basic
    @Column(name = "POINTS")
    private BigInteger points;
    @Basic
    @Column(name = "LIKES")
    private BigInteger likes;
    @Basic
    @Column(name = "CONTENT_DEPRECATED")
    private byte[] content;
    @Basic
    @Column(name = "POINTED")
    private String pointed;
    @Basic
    @Column(name = "REPOST_ID")
    private BigInteger repostId;
    @Basic
    @Column(name = "ORIGINAL_POST_ID")
    private BigInteger originalPostId;

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

    public Date getPost_date() {
        return post_date;
    }

    public void setPost_date(Date postDate) {
        this.post_date = postDate;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
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
        return Objects.equals(postId, that.postId) && Objects.equals(userId, that.userId) && Objects.equals(post_date, that.post_date) && Objects.equals(points, that.points) && Objects.equals(likes, that.likes) && Arrays.equals(content, that.content) && Objects.equals(pointed, that.pointed) && Objects.equals(repostId, that.repostId) && Objects.equals(originalPostId, that.originalPostId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(postId, userId, post_date, points, likes, pointed, repostId, originalPostId);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
