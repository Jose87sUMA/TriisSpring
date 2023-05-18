package com.example.application.data.entities;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Date;
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
    private Date commentDate;
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

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return Objects.equals(commentId, that.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }
}
