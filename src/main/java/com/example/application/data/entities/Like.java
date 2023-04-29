package com.example.application.data.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@jakarta.persistence.Table(name = "LIKES", schema = "UBD3336", catalog = "")
@IdClass(LikesCompositePK.class)
public class Like implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "USER_ID")
    private BigInteger userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "POST_ID")
    private BigInteger postId;

    @EmbeddedId
    LikesCompositePK id = new LikesCompositePK();



    public LikesCompositePK getId() {
        return new LikesCompositePK(userId, postId);
    }

    public void setId(LikesCompositePK key) {
        this.userId = key.getUserId();
        this.postId = key.getPostId();
    }


    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
    public BigInteger getPostId() {
        return postId;
    }

    public void setPostId(BigInteger postId) {
        this.postId = postId;
    }

    public Like(){}
    public Like(BigInteger userId, BigInteger postId){
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like that = (Like) o;
        return Objects.equals(userId, that.userId) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}
