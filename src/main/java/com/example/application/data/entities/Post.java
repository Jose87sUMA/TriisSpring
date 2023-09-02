package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.io.*;
import java.math.BigInteger;
import java.time.*;
import java.util.Arrays;
import java.util.Objects;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    private Instant post_date;
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
    @OneToMany(mappedBy = "reportedPost")
    private List<Report> reports;

    public Post() {
    }

    //FOR POST
    public Post(User user, boolean pointed) {

        this.postId = null;
        this.originalPostId = null;
        this.repostId = null;
        this.post_date = Instant.now();
        this.userId = user.getUserId();
        this.points = BigInteger.ZERO;
        this.pointed = pointed ? "Y":"N";
        this.likes = BigInteger.ZERO;
    }
    //FOR REPOST
    public Post(Post post, User user, boolean pointed) {
        if(post.originalPostId == null) this.originalPostId = post.getPostId();
        else this.originalPostId = post.getOriginalPostId();

        this.postId = null;
        this.repostId = post.getPostId();
        this.post_date = getZonedDate();
        this.userId = user.getUserId();
        this.content = null;
        this.points = BigInteger.ZERO;
        this.pointed = pointed ? "Y":"N";
        this.likes = BigInteger.ZERO;
    }

    private Instant getZonedDate() {
        ZoneId zoneId =  ZoneId.of("Europe/Madrid"); // Use your desired time zone
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
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

    public Instant getPost_date() {
        return post_date;
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
        return Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

}
