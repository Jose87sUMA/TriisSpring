package com.example.application.data.entities;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.*;
import java.util.Objects;

@Entity
@Table(name = "COMMENTS", schema = "UBD3336", catalog = "")
public class Comment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "COMMENT_ID")
    private BigInteger commentId;
    @Basic
    @Column(name = "POST_ID")
    private BigInteger postId;
    @Basic
    @Column(name = "USER_ID")
    private BigInteger userId;
    @Basic
    @Column(name = "USER_COMMENT")
    private String userComment;
    @Basic
    @Column(name = "COMMENT_DATE")
    private Instant commentDate;

    private Instant getZonedDate() {
        ZoneId zoneId =  ZoneId.of("Europe/Madrid"); // Use your desired time zone
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    public BigInteger getCommentId() {
        return commentId;
    }

    public void setCommentId(BigInteger commentId) {
        this.commentId = commentId;
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

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public Instant getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Instant commentDate) {
        this.commentDate = commentDate;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return Objects.equals(postId, that.postId) && Objects.equals(userId, that.userId) && Objects.equals(userComment, that.userComment) && Objects.equals(commentDate, that.commentDate) && Objects.equals(commentId, that.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId, userComment, commentDate, commentId);
    }
}
