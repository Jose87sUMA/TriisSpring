package com.example.application.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Embeddable
public class LikesCompositePK implements Serializable {

    @Column(name = "USER_ID", insertable=false, updatable=false)
    private BigInteger userId;

    @Column(name = "POST_ID", insertable=false, updatable=false)
    private BigInteger postId;

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
    public LikesCompositePK(BigInteger userId, BigInteger postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public LikesCompositePK() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikesCompositePK that = (LikesCompositePK) o;
        return Objects.equals(userId, that.userId) && Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }


}
