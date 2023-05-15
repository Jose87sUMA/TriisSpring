package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "POSTS", schema = "UBD3336", catalog = "")
@DynamicUpdate
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
    @OneToMany(mappedBy = "reportedPost")
    private List<Report> reports;

    public Post() {
    }


    //FOR REPOST
    public Post(Post post, User user, boolean pointed) {
        if(post.originalPostId == null) this.originalPostId = post.getPostId();
        else this.originalPostId = post.getOriginalPostId();

        this.postId = null;
        this.repostId = post.getPostId();
        this.postDate = Date.from(Instant.now());
        this.userId = user.getUserId();
        this.content = null;
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

    /*public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post that = (Post) o;
        return Objects.equals(postId, that.postId) && Objects.equals(userId, that.userId) && Objects.equals(postDate, that.postDate) && Objects.equals(points, that.points) && Objects.equals(likes, that.likes) && Arrays.equals(content, that.content) && Objects.equals(pointed, that.pointed) && Objects.equals(repostId, that.repostId) && Objects.equals(originalPostId, that.originalPostId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(postId, userId, postDate, points, likes, pointed, repostId, originalPostId);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
